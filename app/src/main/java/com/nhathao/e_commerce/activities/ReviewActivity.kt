package com.nhathao.e_commerce.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.adapters.RvAdapterReview
import com.nhathao.e_commerce.databinding.ActivityReviewBinding
import com.nhathao.e_commerce.models.Product
import com.nhathao.e_commerce.models.Rating
import com.nhathao.e_commerce.models.Review
import com.nhathao.e_commerce.models.User
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.max

private lateinit var binding: ActivityReviewBinding

class ReviewActivity : AppCompatActivity() {
    private lateinit var dbRefProduct: DatabaseReference
    private lateinit var dbRefRating: DatabaseReference
    private lateinit var dbRefReview: DatabaseReference
    private lateinit var dialog: BottomSheetDialog
    private lateinit var user: User
    private lateinit var dsRatingSelectedProduct: MutableList<Rating>
    private lateinit var dsReviewSelectedProduct: MutableList<Review>
    private var isLogin: Boolean = false
    private lateinit var productSelected: Product
    private lateinit var ratingUserSelectedProduct: Rating
    private var highestQuantityRating = 0
    private var quantityFiveStar = 0
    private var quantityFourStar = 0
    private var quantityThreeStar = 0
    private var quantityTwoStar = 0
    private var quantityOneStar = 0
    private var ratingUserSelectedProductGetList = listOf<Rating>()
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundleGet = intent.extras
        productSelected = bundleGet?.getSerializable("productSelected") as Product
        isLogin = bundleGet.getBoolean("isLogin")
        if (isLogin)
            user = bundleGet.getSerializable("user") as User

        dbRefProduct = FirebaseDatabase.getInstance().getReference("Products")
        dbRefRating = FirebaseDatabase.getInstance().getReference("Ratings")
        dbRefReview = FirebaseDatabase.getInstance().getReference("Reviews")

        dsRatingSelectedProduct = mutableListOf()

        binding.txtRatingStarAverage.text = productSelected.productRating.toString()

        dbRefRating.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dsRatingSelectedProduct.clear()
                quantityFiveStar = 0
                quantityFourStar = 0
                quantityThreeStar = 0
                quantityTwoStar = 0
                quantityOneStar = 0
                if (snapshot.exists()) {
                    for (ratingSnap in snapshot.children) {
                        val rating = Rating(
                            ratingSnap.child("ratingId").value.toString(),
                            ratingSnap.child("userAccountName").value.toString(),
                            ratingSnap.child("productId").value.toString(),
                            ratingSnap.child("ratingStar").value.toString().toFloat()
                        )
                        if (rating.productId == productSelected.productId) {
                            dsRatingSelectedProduct.add(rating)
                            if (rating.ratingStar == 5f)
                                quantityFiveStar++
                            else if (rating.ratingStar == 4f)
                                quantityFourStar++
                            else if (rating.ratingStar == 3f)
                                quantityThreeStar++
                            else if (rating.ratingStar == 2f)
                                quantityTwoStar++
                            else
                                quantityOneStar++
                        }
                    }
                    highestQuantityRating = maxOf(
                        quantityFiveStar,
                        quantityFourStar,
                        quantityThreeStar,
                        quantityTwoStar,
                        quantityOneStar
                    )
                    xuLyHienThiRatingLine(highestQuantityRating)
                    binding.txtRatingQuantity.text = "${dsRatingSelectedProduct.size} ratings"
                    binding.txtRatingQuantityFiveStar.text = quantityFiveStar.toString()
                    binding.txtRatingQuantityFourStar.text = quantityFourStar.toString()
                    binding.txtRatingQuantityThreeStar.text = quantityThreeStar.toString()
                    binding.txtRatingQuantityTwoStar.text = quantityTwoStar.toString()
                    binding.txtRatingQuantityOneStar.text = quantityOneStar.toString()
                    if (isLogin) {
                        ratingUserSelectedProductGetList =
                            dsRatingSelectedProduct.filter { it.userAccountName == user.userAccountName }
                        if (ratingUserSelectedProductGetList.isNotEmpty()) {
                            ratingUserSelectedProduct = ratingUserSelectedProductGetList[0]
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        dsReviewSelectedProduct = mutableListOf()

        dbRefReview.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dsReviewSelectedProduct.clear()
                if (snapshot.exists()) {
                    for (reviewSnap in snapshot.children) {
                        val review = Review(
                            reviewSnap.child("reviewId").value.toString(),
                            reviewSnap.child("userAccountName").value.toString(),
                            reviewSnap.child("productId").value.toString(),
                            reviewSnap.child("reviewContent").value.toString(),
                            reviewSnap.child("reviewDate").value.toString().toLong()
                        )
                        if (review.productId == productSelected.productId)
                            dsReviewSelectedProduct.add(review)
                    }
                    binding.txtReviewQuantity.text = "${dsReviewSelectedProduct.size} reviews"
                    val reviewAdapter = RvAdapterReview(dsReviewSelectedProduct)
                    binding.rcvReview.adapter = reviewAdapter
                    binding.rcvReview.layoutManager = LinearLayoutManager(
                        this@ReviewActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        binding.btnBack.setOnClickListener {
            val data = Intent()
            val bundlePassing = Bundle()
            bundlePassing.putBoolean("isLogin", isLogin)
            if (isLogin)
                bundlePassing.putSerializable("user", user)
            data.putExtras(bundlePassing)
            setResult(Activity.RESULT_OK, data)
            finish()
        }

        binding.btnWriteReview.setOnClickListener {
            if (isLogin)
                showBottomSheetReview()
            else {
                val intent = Intent(this, LoginActivity::class.java)
                startForResult.launch(intent)
            }
        }
    }

    private fun xuLyHienThiRatingLine(quantity: Int) {
        if (quantity > 0) {
            val widthMax = 380
            var widthAverage = 0
            if (quantity == quantityFiveStar) {
                widthAverage = widthMax / quantityFiveStar
                binding.ratingLineFiveStar.layoutParams.width = widthMax
                binding.ratingLineFourStar.layoutParams.width = widthAverage * quantityFourStar
                binding.ratingLineThreeStar.layoutParams.width = widthAverage * quantityThreeStar
                binding.ratingLineTwoStar.layoutParams.width = widthAverage * quantityTwoStar
                binding.ratingLineOneStar.layoutParams.width = widthAverage * quantityOneStar
            } else if (quantity == quantityFourStar) {
                widthAverage = widthMax / quantityFourStar
                binding.ratingLineFiveStar.layoutParams.width = widthAverage * quantityFiveStar
                binding.ratingLineFourStar.layoutParams.width = widthMax
                binding.ratingLineThreeStar.layoutParams.width = widthAverage * quantityThreeStar
                binding.ratingLineTwoStar.layoutParams.width = widthAverage * quantityTwoStar
                binding.ratingLineOneStar.layoutParams.width = widthAverage * quantityOneStar
            } else if (quantity == quantityThreeStar) {
                widthAverage = widthMax / quantityThreeStar
                binding.ratingLineFiveStar.layoutParams.width = widthAverage * quantityFiveStar
                binding.ratingLineFourStar.layoutParams.width = widthAverage * quantityFourStar
                binding.ratingLineThreeStar.layoutParams.width = widthMax
                binding.ratingLineTwoStar.layoutParams.width = widthAverage * quantityTwoStar
                binding.ratingLineOneStar.layoutParams.width = widthAverage * quantityOneStar
            } else if (quantity == quantityTwoStar) {
                widthAverage = widthMax / quantityTwoStar
                binding.ratingLineFiveStar.layoutParams.width = widthAverage * quantityFiveStar
                binding.ratingLineFourStar.layoutParams.width = widthAverage * quantityFourStar
                binding.ratingLineThreeStar.layoutParams.width = widthAverage * quantityThreeStar
                binding.ratingLineTwoStar.layoutParams.width = widthMax
                binding.ratingLineOneStar.layoutParams.width = widthAverage * quantityOneStar
            } else {
                widthAverage = widthMax / quantityOneStar
                binding.ratingLineFiveStar.layoutParams.width = widthAverage * quantityFiveStar
                binding.ratingLineFourStar.layoutParams.width = widthAverage * quantityFourStar
                binding.ratingLineThreeStar.layoutParams.width = widthAverage * quantityThreeStar
                binding.ratingLineTwoStar.layoutParams.width = widthAverage * quantityTwoStar
                binding.ratingLineOneStar.layoutParams.width = widthMax
            }
        }
        if (quantityFiveStar == 0)
            binding.ratingLineFiveStar.layoutParams.width = 18
        if (quantityFourStar == 0)
            binding.ratingLineFourStar.layoutParams.width = 18
        if (quantityThreeStar == 0)
            binding.ratingLineThreeStar.layoutParams.width = 18
        if (quantityTwoStar == 0)
            binding.ratingLineTwoStar.layoutParams.width = 18
        if (quantityOneStar == 0)
            binding.ratingLineOneStar.layoutParams.width = 18
    }

    private fun showBottomSheetReview() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_review, null)
        dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)
        val edtReviewContent = dialogView.findViewById<EditText>(R.id.edtReviewContent)
        val btnSendReview = dialogView.findViewById<Button>(R.id.btnSendReview)

        if (ratingUserSelectedProductGetList.isNotEmpty())
            ratingBar.rating = ratingUserSelectedProduct.ratingStar

        btnSendReview.setOnClickListener {
            if (ratingBar.rating != 0f) {
                //Insert Rating
                if (ratingUserSelectedProductGetList.isEmpty()) {
                    val rating = Rating(
                        "",
                        user.userAccountName,
                        productSelected.productId,
                        ratingBar.rating
                    )
                    rating.ratingId = dbRefRating.push().key.toString()
                    dbRefRating.child(rating.ratingId).setValue(rating)
                    dsRatingSelectedProduct.add(rating)
                    dbRefProduct.child(productSelected.productId).child("productRatingQuantity")
                        .setValue(productSelected.productRatingQuantity?.plus(1))
                }
                //Update Rating
                else {
                    ratingUserSelectedProduct.ratingStar = ratingBar.rating
                    dbRefRating.child(ratingUserSelectedProduct.ratingId)
                        .setValue(ratingUserSelectedProduct)
                    dsRatingSelectedProduct.find { it.ratingId == ratingUserSelectedProduct.ratingId }?.ratingStar =
                        ratingUserSelectedProduct.ratingStar
                }
                //Tinh lai ratingStar cua Product duoc lua chon
                var sumRating = 0f
                for (ratingChildren in dsRatingSelectedProduct) {
                    sumRating += ratingChildren.ratingStar
                }
                var ratingStarProduct = sumRating / dsRatingSelectedProduct.size
                ratingStarProduct = String.format("%.3f", ratingStarProduct).toFloat()
                ratingStarProduct = String.format("%.2f", ratingStarProduct).toFloat()
                ratingStarProduct = String.format("%.1f", ratingStarProduct).toFloat()
                dbRefProduct.child(productSelected.productId).child("productRating")
                    .setValue(ratingStarProduct)
                binding.txtRatingStarAverage.text = ratingStarProduct.toString()

                //Insert Review
                if (edtReviewContent.text.isNotEmpty()) {
                    val review = Review(
                        "",
                        user.userAccountName,
                        productSelected.productId,
                        edtReviewContent.text.toString(),
                        calendar.timeInMillis
                    )
                    review.reviewId = dbRefReview.push().key.toString()
                    dbRefReview.child(review.reviewId).setValue(review)
                }
                dialog.dismiss()
            } else
                Toast.makeText(this, "Please rate it", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val bundleGet = intent?.extras
                if (bundleGet != null) {
                    isLogin = bundleGet.getBoolean("isLogin")
                    user = bundleGet.getSerializable("user") as User
                    ratingUserSelectedProductGetList =
                        dsRatingSelectedProduct.filter { it.userAccountName == user.userAccountName }
                    if (ratingUserSelectedProductGetList.isNotEmpty()) {
                        ratingUserSelectedProduct = ratingUserSelectedProductGetList[0]
                    }
                }
            } else {
                Toast.makeText(this, "You must login", Toast.LENGTH_SHORT).show()
            }
        }
}