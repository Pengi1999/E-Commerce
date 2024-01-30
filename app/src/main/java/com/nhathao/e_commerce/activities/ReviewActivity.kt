package com.nhathao.e_commerce.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivityReviewBinding
import com.nhathao.e_commerce.models.Product
import com.nhathao.e_commerce.models.Rating
import com.nhathao.e_commerce.models.User

private lateinit var binding: ActivityReviewBinding
class ReviewActivity : AppCompatActivity() {
    private lateinit var dbRefProduct : DatabaseReference
    private lateinit var dbRefUser : DatabaseReference
    private lateinit var dbRefRating : DatabaseReference
    private lateinit var dbRefReview : DatabaseReference
    private lateinit var dialog: BottomSheetDialog
    private var user: User? = null
    private lateinit var productSelected: Product
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras

        if (bundle!!.getSerializable("user") != null)
            user = bundle.getSerializable("user") as User
        productSelected = bundle.getSerializable("productSelected") as Product

        dbRefRating = FirebaseDatabase.getInstance().getReference("Ratings")
        dbRefReview = FirebaseDatabase.getInstance().getReference("Reviews")

        var a = binding.ratingLineFiveStar.layoutParams.width / binding.txtRatingQuantityFiveStar.text.toString().toInt()
        binding.ratingLineFourStar.layoutParams.width = a * binding.txtRatingQuantityFourStar.text.toString().toInt()
        binding.ratingLineThreeStar.layoutParams.width = a * binding.txtRatingQuantityThreeStar.text.toString().toInt()
        binding.ratingLineTwoStar.layoutParams.width = a * binding.txtRatingQuantityTwoStar.text.toString().toInt()
        binding.ratingLineOneStar.layoutParams.width = a * binding.txtRatingQuantityOneStar.text.toString().toInt()
        binding.ratingLineOneStar.layoutParams.width = 18

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnWriteReview.setOnClickListener {
            if(user != null)
                showBottomSheetReview()
            else
            {

            }
        }
    }

    private fun showBottomSheetReview() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_review, null)
        dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        val btnSendReview = dialogView.findViewById<Button>(R.id.btnSendReview)

        btnSendReview.setOnClickListener {
//            val rating = Rating("")
//            product.productId = dbRef.push().key.toString()
//            dbRef.child(product.productId).setValue(product)
            dialog.dismiss()
        }
        dialog.show()
    }
}