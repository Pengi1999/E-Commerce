package com.nhathao.e_commerce.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    private var requestCodeSignUp = 1
    private var requestCodeHaveAccount = 2
    private var requestCodeForgotPWD = 3
    private var requestCodeChangeAccount = 4
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadAccount()

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        binding.layoutEdtAccountName.isEndIconVisible = false
        binding.btnBack.setOnClickListener {
            val data = Intent()
            val bundlePassing = Bundle()
            bundlePassing.putBoolean("isLogin", false)
            data.putExtras(bundlePassing)
            setResult(Activity.RESULT_CANCELED, data)
            finish()
        }

        binding.areaForgotPWD.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startForResult.launch(intent)
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
            startForResult.launch(intent)
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
                        val data = Intent()
                        val bundlePassing = Bundle()
                        bundlePassing.putInt("requestCode", requestCodeChangeAccount)
                        bundlePassing.putSerializable("user", user)
                        bundlePassing.putBoolean("isLogin", true)
                        data.putExtras(bundlePassing)
                        setResult(Activity.RESULT_OK, data)
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

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val bundleGet = intent?.extras
                if (bundleGet != null) {
                    val requestCode = bundleGet.getInt("requestCode", 0)
                    if (requestCode == requestCodeHaveAccount) {
                        binding.layoutEdtAccountName.editText?.text?.clear()
                        binding.layoutEdtPWD.editText?.text?.clear()
                    } else if (requestCode == requestCodeSignUp || requestCode == requestCodeForgotPWD) {
                        val user = bundleGet.getSerializable("user") as User
                        binding.layoutEdtAccountName.editText?.setText(user.userAccountName)
                        binding.layoutEdtPWD.editText?.setText(user.userPWD)
                    }
                }
            }
        }

}