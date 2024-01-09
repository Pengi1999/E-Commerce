package com.nhathao.e_commerce.activities

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivityLoginBinding
import com.nhathao.e_commerce.models.User

private lateinit var binding: ActivityLoginBinding
class LoginActivity : AppCompatActivity() {
    private lateinit var dbRef : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        binding.layoutEdtAccountName.isEndIconVisible = false

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.areaForgotPWD.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val userAccountName = binding.layoutEdtAccountName.editText?.text.toString()
            val userPWD = binding.layoutEdtPWD.editText?.text.toString()

            val isNotEmpty = checkEmpty(userAccountName,userPWD)

            if (isNotEmpty){
                dbRef.child(userAccountName).get()
                    .addOnSuccessListener {
                        if (it.exists()) {
                            val user = User(
                                it.child("userAccountName").value.toString(),
                                it.child("userPWD").value.toString(),
                                it.child("userName").value.toString(),
                                it.child("secretCode").value.toString()
                            )
                            if (user.userPWD == userPWD) {
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                            else{
                                binding.layoutEdtPWD.error = "Password doesn't match"
                            }
                        }
                        else {
                            binding.layoutEdtAccountName.error = "Account doesn't exist"
                        }
                    }
                    .addOnFailureListener {err ->
                        Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }

        binding.btnLoginByFacebook.setOnClickListener {

        }

        binding.btnLoginByGoogle.setOnClickListener {

        }

        binding.txtSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.layoutEdtAccountName.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtAccountName.error = ""
        }

        binding.layoutEdtPWD.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtPWD.error = ""
        }

    }

    private fun checkEmpty(userAccountName: String, userPWD: String): Boolean {
        var isNotEmpty = true

        if (userAccountName.isEmpty()){
            isNotEmpty = false
            binding.layoutEdtAccountName.error = "Field can't be empty"
            binding.layoutEdtAccountName.isEndIconVisible = false
        }
        else {
            binding.layoutEdtAccountName.error = ""
            binding.layoutEdtAccountName.isEndIconVisible = true
        }
        if (userPWD.isEmpty()){
            isNotEmpty = false
            binding.layoutEdtPWD.error = "Field can't be empty"
        }
        else {
            binding.layoutEdtPWD.error = ""
        }
        return isNotEmpty
    }
}