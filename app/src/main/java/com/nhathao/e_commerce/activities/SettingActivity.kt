package com.nhathao.e_commerce.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivitySettingBinding
import com.nhathao.e_commerce.models.User
import java.util.Calendar

private lateinit var binding: ActivitySettingBinding
class SettingActivity : AppCompatActivity() {
    private lateinit var dbRef : DatabaseReference
    private lateinit var dialog: BottomSheetDialog
    private lateinit var layoutEdtOldPWD: TextInputLayout
    private lateinit var layoutEdtNewPWD: TextInputLayout
    private lateinit var layoutEdtRepeatNewPWD: TextInputLayout
    private lateinit var btnSavePWD: Button
    private lateinit var areaForgotPWD: LinearLayout
    private lateinit var layoutEdtOldSecretCode: TextInputLayout
    private lateinit var layoutEdtNewSecretCode: TextInputLayout
    private lateinit var layoutEdtRepeatNewSecretCode: TextInputLayout
    private lateinit var btnSaveSecretCode: Button
    private lateinit var user: User
    private lateinit var userAccountName : String
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        val bundle = intent.extras
        user = bundle?.getSerializable("user") as User

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSearch.setOnClickListener {

        }

        binding.layoutEdtFullName.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {

                if (binding.layoutEdtFullName.editText!!.text.isEmpty())
                {
                    binding.layoutEdtFullName.editText!!.setText(user.userName)
                }
                else{
                    dbRef.child(user.userAccountName).child("userName").setValue(binding.layoutEdtFullName.editText?.text.toString())
                    user.userName = binding.layoutEdtFullName.editText?.text.toString()
                }
            }
        }

        binding.layoutEdtBirthday.editText?.setOnClickListener {
            val today = Calendar.getInstance()
            val todayYear = today.get(Calendar.YEAR)
            val todayMonth = today.get(Calendar.MONTH)
            val todayDate = today.get(Calendar.DAY_OF_MONTH)
            val dp = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                dbRef.child(user.userAccountName).child("birthday").setValue("$dayOfMonth/${month+1}/$year")
                binding.layoutEdtBirthday.editText?.setText("$dayOfMonth/${month+1}/$year")
                user.birthday = binding.layoutEdtBirthday.editText?.text.toString()
            },todayYear, todayMonth, todayDate)
            dp.datePicker.maxDate = System.currentTimeMillis()
            dp.show()
        }

        binding.txtChangePWD.setOnClickListener {
            showBottomSheetChangePWD()
        }

        binding.txtChangeSecretCode.setOnClickListener {
            showBottomSheetChangeSecretCode()
        }
    }

    private fun showBottomSheetChangeSecretCode() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_changesecretcode, null)
        dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)
        layoutEdtOldSecretCode = dialogView.findViewById<TextInputLayout>(R.id.layoutEdtOldSecretCode)
        layoutEdtNewSecretCode = dialogView.findViewById<TextInputLayout>(R.id.layoutEdtNewSecretCode)
        layoutEdtRepeatNewSecretCode = dialogView.findViewById<TextInputLayout>(R.id.layoutEdtRepeatNewSecretCode)
        btnSaveSecretCode = dialogView.findViewById<Button>(R.id.btnSaveSecretCode)

        btnSaveSecretCode.setOnClickListener {
            val oldSecretCode = layoutEdtOldSecretCode.editText?.text.toString()
            val newSecretCode = layoutEdtNewSecretCode.editText?.text.toString()
            val repeatNewSecretCode = layoutEdtRepeatNewSecretCode.editText?.text.toString()

            val isNotEmpty = checkEmptyChangeSecretCode(oldSecretCode, newSecretCode, repeatNewSecretCode)

            if (isNotEmpty) {
                if (user.secretCode == oldSecretCode) {
                    if (newSecretCode == repeatNewSecretCode) {
                        dbRef.child(user.userAccountName).child("secretCode").setValue(newSecretCode)
                        user.secretCode = newSecretCode
                        binding.layoutEdtSecretCode.editText?.setText(newSecretCode)
                        dialog.dismiss()
                    }
                    else {
                        layoutEdtRepeatNewSecretCode.error = "Secret codes do not match"
                    }
                }
                else {
                    layoutEdtOldSecretCode.error = "Your old secret code is incorrect"
                }
            }
        }

        layoutEdtOldSecretCode.editText?.doOnTextChanged { text, start, before, count ->
            layoutEdtOldSecretCode.error = ""
        }

        layoutEdtNewSecretCode.editText?.doOnTextChanged { text, start, before, count ->
            layoutEdtNewSecretCode.error = ""
        }

        layoutEdtRepeatNewSecretCode.editText?.doOnTextChanged { text, start, before, count ->
            layoutEdtRepeatNewSecretCode.error = ""
        }
        dialog.show()
    }

    private fun showBottomSheetChangePWD() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_changepwd, null)
        dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)
        layoutEdtOldPWD = dialogView.findViewById<TextInputLayout>(R.id.layoutEdtOldPWD)
        layoutEdtNewPWD = dialogView.findViewById<TextInputLayout>(R.id.layoutEdtNewPWD)
        layoutEdtRepeatNewPWD = dialogView.findViewById<TextInputLayout>(R.id.layoutEdtRepeatNewPWD)
        btnSavePWD = dialogView.findViewById<Button>(R.id.btnSavePWD)
        areaForgotPWD = dialogView.findViewById<LinearLayout>(R.id.areaForgotPWD)

        btnSavePWD.setOnClickListener {
            val oldPWD = layoutEdtOldPWD.editText?.text.toString()
            val newPWD = layoutEdtNewPWD.editText?.text.toString()
            val repeatNewPWD = layoutEdtRepeatNewPWD.editText?.text.toString()

            val isNotEmpty = checkEmptyChangePWD(oldPWD, newPWD, repeatNewPWD)

            if (isNotEmpty) {
                if (user.userPWD == oldPWD) {
                    if (newPWD == repeatNewPWD) {
                        dbRef.child(user.userAccountName).child("userPWD").setValue(newPWD)
                        user.userPWD = newPWD
                        binding.layoutEdtPWD.editText?.setText(newPWD)
                        dialog.dismiss()
                    }
                    else {
                        layoutEdtRepeatNewPWD.error = "Passwords do not match"
                    }
                }
                else {
                    layoutEdtOldPWD.error = "Your old password is incorrect"
                }
            }
        }

        areaForgotPWD.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        layoutEdtOldPWD.editText?.doOnTextChanged { text, start, before, count ->
            layoutEdtOldPWD.error = ""
        }

        layoutEdtNewPWD.editText?.doOnTextChanged { text, start, before, count ->
            layoutEdtNewPWD.error = ""
        }

        layoutEdtRepeatNewPWD.editText?.doOnTextChanged { text, start, before, count ->
            layoutEdtRepeatNewPWD.error = ""
        }
        dialog.show()
    }

    private fun checkEmptyChangePWD(oldPWD: String, newPWD: String, repeatNewPWD: String): Boolean {
        var isNotEmpty = true

        if (oldPWD.isEmpty()) {
            isNotEmpty = false
            layoutEdtOldPWD.error = "Field can't be empty"
        } else {
            layoutEdtOldPWD.error = ""
        }
        if (newPWD.isEmpty()) {
            isNotEmpty = false
            layoutEdtNewPWD.error = "Field can't be empty"
        } else {
            layoutEdtNewPWD.error = ""
        }

        if (repeatNewPWD.isEmpty()) {
            isNotEmpty = false
            layoutEdtRepeatNewPWD.error = "Field can't be empty"
        } else {
            layoutEdtRepeatNewPWD.error = ""
        }
        return isNotEmpty
    }

    private fun checkEmptyChangeSecretCode(oldSecretCode: String, newSecretCode: String, repeatNewSecretCode: String): Boolean {
        var isNotEmpty = true

        if (oldSecretCode.isEmpty()) {
            isNotEmpty = false
            layoutEdtOldSecretCode.error = "Field can't be empty"
        } else {
            layoutEdtOldSecretCode.error = ""
        }
        if (newSecretCode.isEmpty()) {
            isNotEmpty = false
            layoutEdtNewSecretCode.error = "Field can't be empty"
        } else {
            layoutEdtNewSecretCode.error = ""
        }

        if (repeatNewSecretCode.isEmpty()) {
            isNotEmpty = false
            layoutEdtRepeatNewSecretCode.error = "Field can't be empty"
        } else {
            layoutEdtRepeatNewSecretCode.error = ""
        }
        return isNotEmpty
    }

    private fun loadUserProfile() {
        binding.layoutEdtFullName.editText?.setText(user.userName)
        binding.layoutEdtBirthday.editText?.setText(user.birthday)
        binding.layoutEdtPWD.editText?.setText(user.userPWD)
        binding.layoutEdtSecretCode.editText?.setText(user.secretCode)
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }
}