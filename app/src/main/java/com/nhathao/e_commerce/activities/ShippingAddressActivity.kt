package com.nhathao.e_commerce.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivityShippingAddressBinding

private lateinit var binding: ActivityShippingAddressBinding
class ShippingAddressActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShippingAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAdd.setOnClickListener {
            Toast.makeText(this, "Do it later", Toast.LENGTH_SHORT).show()
        }
    }
}