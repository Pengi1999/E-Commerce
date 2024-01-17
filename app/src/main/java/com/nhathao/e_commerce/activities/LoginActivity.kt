package com.nhathao.e_commerce.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivityLoginBinding
import com.nhathao.e_commerce.models.User

private lateinit var binding: ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private var accountName: String? = null
    private var password: String? = null
    private var isRemember: Boolean? = null
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

            val isNotEmpty = checkEmpty(userAccountName, userPWD)

            if (isNotEmpty) {
                Login(userAccountName, userPWD)
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

    private fun Login(userAccountName: String, userPWD: String) {
        dbRef.child(userAccountName).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val user = User(
                        it.child("userAccountName").value.toString(),
                        it.child("userPWD").value.toString(),
                        it.child("userName").value.toString(),
                        it.child("secretCode").value.toString(),
                        it.child("birthday").value.toString(),
                        it.child("avatar").value.toString(),
                        it.child("email").value.toString()
                    )
                    if (user.userPWD == userPWD) {
                        saveAccount()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        val bundle = Bundle()
                        bundle.putSerializable("user", user)
                        bundle.putBoolean("isLogin", true)
                        intent.putExtras(bundle)
                        startActivity(intent)
                        finish()
                    } else {
                        binding.layoutEdtPWD.error = "Password doesn't match"
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

    private fun saveAccount() {
        isRemember = binding.chkRemember.isChecked
        sharedPreferences = this.getSharedPreferences("saveAccount", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("key_isRemember", isRemember!!)
        if (isRemember!!) {
            accountName = binding.layoutEdtAccountName.editText?.text.toString()
            password = binding.layoutEdtPWD.editText?.text.toString()
            editor.putString("key_accountName", accountName)
            editor.putString("key_password", password)
        }
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        loadAccount()
    }

    private fun loadAccount() {
        sharedPreferences = this.getSharedPreferences("saveAccount", Context.MODE_PRIVATE)
        isRemember = sharedPreferences.getBoolean("key_isRemember", false)
        binding.chkRemember.isChecked = isRemember!!
        if (isRemember!!) {
            accountName = sharedPreferences.getString("key_accountName", "")
            password = sharedPreferences.getString("key_password", "")

            binding.layoutEdtAccountName.editText?.setText(accountName)
            binding.layoutEdtPWD.editText?.setText(password)
        }
    }

    private fun checkEmpty(userAccountName: String, userPWD: String): Boolean {
        var isNotEmpty = true

        if (userAccountName.isEmpty()) {
            isNotEmpty = false
            binding.layoutEdtAccountName.error = "Field can't be empty"
            binding.layoutEdtAccountName.isEndIconVisible = false
        } else {
            binding.layoutEdtAccountName.error = ""
            binding.layoutEdtAccountName.isEndIconVisible = true
        }
        if (userPWD.isEmpty()) {
            isNotEmpty = false
            binding.layoutEdtPWD.error = "Field can't be empty"
        } else {
            binding.layoutEdtPWD.error = ""
        }
        return isNotEmpty
    }
}