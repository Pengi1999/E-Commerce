package com.nhathao.e_commerce.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.nhathao.e_commerce.databinding.ActivityVisualSearchBinding

class VisualSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVisualSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisualSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnTakeAPhoto.setOnClickListener {
            val intent = Intent(this, TakingAPhotoActivity::class.java)
            startActivity(intent)
        }

        binding.btnUploadAnImage.setOnClickListener {
            Toast.makeText(this, "Do it later", Toast.LENGTH_SHORT).show()
        }
    }
}