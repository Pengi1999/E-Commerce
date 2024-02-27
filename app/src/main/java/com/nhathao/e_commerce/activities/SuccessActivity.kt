package com.nhathao.e_commerce.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivitySuccessBinding

private lateinit var binding: ActivitySuccessBinding
class SuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnContinueShopping.setOnClickListener {
            finish()
        }
    }
}