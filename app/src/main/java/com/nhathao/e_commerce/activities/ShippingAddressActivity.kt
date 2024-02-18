package com.nhathao.e_commerce.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivityShippingAddressBinding

private lateinit var binding: ActivityShippingAddressBinding
class ShippingAddressActivity : AppCompatActivity() {
    private lateinit var dbRefShippingAddress: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShippingAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRefShippingAddress = FirebaseDatabase.getInstance().getReference("ShippingAddresses")

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AddingShippingAddressActivity::class.java)
            startActivity(intent)
        }

    }
}