package com.nhathao.e_commerce.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivitySignUpBinding
import com.nhathao.e_commerce.models.User

private lateinit var binding: ActivitySignUpBinding
class SignUpActivity : AppCompatActivity() {
    private lateinit var dbRef : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        binding.layoutEdtName.isEndIconVisible = false
        binding.layoutEdtAccountName.isEndIconVisible = false

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.areaHaveAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignUp.setOnClickListener {
            val userName = binding.layoutEdtName.editText?.text.toString()
            val userAccountName = binding.layoutEdtAccountName.editText?.text.toString()
            val userPWD = binding.layoutEdtPWD.editText?.text.toString()
            val confirmPWD = binding.layoutEdtConfirmPWD.editText?.text.toString()
            val userSecretCode = binding.layoutEdtSecretCode.editText?.text.toString()

            val user = User(userAccountName, userPWD, userName, userSecretCode)

            val isNotEmpty = checkEmpty(userName,userAccountName,userPWD,confirmPWD,userSecretCode)

            if (isNotEmpty){
                if (userPWD == confirmPWD) {
                    insertUser(user)
                }
                else {
                    binding.layoutEdtConfirmPWD.error = "Confirm Password doesn't match with Password"
                }
            }
        }

        binding.btnSignUpByGoogle.setOnClickListener {

        }

        binding.btnSignUpByFacebook.setOnClickListener {

        }

        binding.layoutEdtName.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtName.error = ""
        }

        binding.layoutEdtAccountName.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtAccountName.error = ""
        }

        binding.layoutEdtPWD.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtPWD.error = ""
        }

        binding.layoutEdtConfirmPWD.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtConfirmPWD.error = ""
        }

        binding.layoutEdtSecretCode.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtSecretCode.error = ""
        }
    }

    private fun insertUser(user: User) {
        // Check Account already exists
        dbRef.child(user.userAccountName).get()
            .addOnSuccessListener {
                if(it.value != null)
                    binding.layoutEdtAccountName.error = "Account already exists"
                else {
                    //Insert User
                    dbRef.child(user.userAccountName).setValue(user)
                        .addOnCompleteListener {
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener { err ->
                            Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }
            .addOnFailureListener { err ->
                    Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_SHORT)
                        .show()
            }
    }

    private fun checkEmpty(userName: String, userAccountName: String, userPWD: String, confirmPWD: String, userSecretCode: String): Boolean {
        var isNotEmpty = true
        if (userName.isEmpty()){
            isNotEmpty = false
            binding.layoutEdtName.error = "Field can't be empty"
            binding.layoutEdtName.isEndIconVisible = false
        }
        else {
            binding.layoutEdtName.error = ""
            binding.layoutEdtName.isEndIconVisible = true
        }
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
        if (confirmPWD.isEmpty()){
            isNotEmpty = false
            binding.layoutEdtConfirmPWD.error = "Field can't be empty"
        }
        else {
            binding.layoutEdtConfirmPWD.error = ""
        }
        if (userSecretCode.isEmpty()){
            isNotEmpty = false
            binding.layoutEdtSecretCode.error = "Field can't be empty"
        }
        else {
            binding.layoutEdtSecretCode.error = ""
        }
        return isNotEmpty
    }
}