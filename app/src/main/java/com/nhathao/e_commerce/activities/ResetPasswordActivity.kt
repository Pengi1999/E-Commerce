package com.nhathao.e_commerce.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivityResetPasswordBinding
import com.nhathao.e_commerce.models.User

private lateinit var binding: ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        val bundleGet = intent.extras
        val user = bundleGet?.getSerializable("user") as User

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnConfirmResetPWD.setOnClickListener {
            val newPWD = binding.layoutEdtNewPWD.editText?.text.toString()
            val confirmNewPWD = binding.layoutEdtConfirmNewPWD.editText?.text.toString()

            val isNotEmpty = checkEmpty(newPWD, confirmNewPWD)

            if (isNotEmpty) {
                if (newPWD == confirmNewPWD) {
                    dbRef.child(user.userAccountName).child("userPWD").setValue(confirmNewPWD)
                    val data = Intent()
                    val bundlePassing = Bundle()
                    bundlePassing.putSerializable("user", user)
                    data.putExtras(bundlePassing)
                    setResult(Activity.RESULT_OK, data)
                    finish()
                } else {
                    binding.layoutEdtConfirmNewPWD.error =
                        "Confirm Password doesn't match with Password"
                }
            }
        }

        binding.layoutEdtNewPWD.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtNewPWD.error = ""
        }

        binding.layoutEdtConfirmNewPWD.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtConfirmNewPWD.error = ""
        }
    }

    private fun checkEmpty(newPWD: String, confirmNewPWD: String): Boolean {
        var isNotEmpty = true

        if (newPWD.isEmpty()) {
            isNotEmpty = false
            binding.layoutEdtNewPWD.error = "Field can't be empty"
        } else {
            binding.layoutEdtNewPWD.error = ""
        }
        if (confirmNewPWD.isEmpty()) {
            isNotEmpty = false
            binding.layoutEdtConfirmNewPWD.error = "Field can't be empty"
        } else {
            binding.layoutEdtConfirmNewPWD.error = ""
        }
        return isNotEmpty
    }
}