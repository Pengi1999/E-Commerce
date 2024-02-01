package com.nhathao.e_commerce.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var dbRefProduct: DatabaseReference
    private lateinit var dbRefUser: DatabaseReference
    private lateinit var dbRefRating: DatabaseReference
    private lateinit var dbRefReview: DatabaseReference
    private lateinit var dialog: BottomSheetDialog
    private lateinit var user: User
    private var isLogin: Boolean = false
    private lateinit var productSelected: Product
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundleGet = intent.extras
        productSelected = bundleGet?.getSerializable("productSelected") as Product
        isLogin = bundleGet.getBoolean("isLogin")
        if (isLogin)
            user = bundleGet.getSerializable("user") as User

        dbRefRating = FirebaseDatabase.getInstance().getReference("Ratings")
        dbRefReview = FirebaseDatabase.getInstance().getReference("Reviews")

        var a =
            binding.ratingLineFiveStar.layoutParams.width / binding.txtRatingQuantityFiveStar.text.toString()
                .toInt()
        binding.ratingLineFourStar.layoutParams.width =
            a * binding.txtRatingQuantityFourStar.text.toString().toInt()
        binding.ratingLineThreeStar.layoutParams.width =
            a * binding.txtRatingQuantityThreeStar.text.toString().toInt()
        binding.ratingLineTwoStar.layoutParams.width =
            a * binding.txtRatingQuantityTwoStar.text.toString().toInt()
        binding.ratingLineOneStar.layoutParams.width =
            a * binding.txtRatingQuantityOneStar.text.toString().toInt()
        binding.ratingLineOneStar.layoutParams.width = 18

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

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val bundleGet = intent?.extras
                if (bundleGet != null) {
                    isLogin = bundleGet.getBoolean("isLogin")
                    user = bundleGet.getSerializable("user") as User
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "You must login", Toast.LENGTH_SHORT).show()
            }
        }
}