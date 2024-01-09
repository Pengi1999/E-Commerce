package com.nhathao.e_commerce.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nhathao.e_commerce.databinding.ActivityForgotPasswordBinding
import com.nhathao.e_commerce.models.User

private lateinit var binding: ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        binding.layoutEdtAccountName.isEndIconVisible = false

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnResetPWD.setOnClickListener {
            val userAccountName = binding.layoutEdtAccountName.editText?.text.toString()
            val secretCode = binding.layoutEdtSecretCode.editText?.text.toString()

            val isNotEmpty = checkEmpty(userAccountName, secretCode)

            if (isNotEmpty) {
                resetPassword(userAccountName, secretCode)
            }
        }

        binding.layoutEdtAccountName.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtAccountName.error = ""
        }

        binding.layoutEdtSecretCode.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtSecretCode.error = ""
        }
    }

    private fun resetPassword(userAccountName: String, secretCode: String) {
        dbRef.child(userAccountName).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val user = User(
                        it.child("userAccountName").value.toString(),
                        it.child("userPWD").value.toString(),
                        it.child("userName").value.toString(),
                        it.child("secretCode").value.toString(),
                        it.child("birthday").value.toString()
                    )
                    if (user.secretCode == secretCode) {
                        val intent = Intent(this, ResetPasswordActivity::class.java)
                        val bundle = Bundle()
                        bundle.putSerializable("user", user)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    } else {
                        binding.layoutEdtSecretCode.error = "SecretCode doesn't match"
                    }
                } else {
                    binding.layoutEdtAccountName.error = "Account doesn't exist"
                }
            }
            .addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun checkEmpty(userAccountName: String, secretCode: String): Boolean {
        var isNotEmpty = true

        if (userAccountName.isEmpty()) {
            isNotEmpty = false
            binding.layoutEdtAccountName.error = "Field can't be empty"
            binding.layoutEdtAccountName.isEndIconVisible = false
        } else {
            binding.layoutEdtAccountName.error = ""
            binding.layoutEdtAccountName.isEndIconVisible = true
        }
        if (secretCode.isEmpty()) {
            isNotEmpty = false
            binding.layoutEdtSecretCode.error = "Field can't be empty"
        } else {
            binding.layoutEdtSecretCode.error = ""
        }
        return isNotEmpty
    }
}