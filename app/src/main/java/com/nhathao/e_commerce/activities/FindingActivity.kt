package com.nhathao.e_commerce.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivityFindingBinding

class FindingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFindingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}