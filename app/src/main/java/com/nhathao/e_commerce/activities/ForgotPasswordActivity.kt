package com.nhathao.e_commerce.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivityForgotPasswordBinding

private lateinit var binding: ActivityForgotPasswordBinding
class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}