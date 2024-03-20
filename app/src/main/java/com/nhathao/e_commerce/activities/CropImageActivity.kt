package com.nhathao.e_commerce.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.FileProvider
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivityCropImageBinding
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File

class CropImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCropImageBinding
    private lateinit var file: File
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCropImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        file = intent.getSerializableExtra("imageFile") as File
        uri = FileProvider.getUriForFile(this, this.applicationContext.packageName + ".provider", file)

        Picasso.with(this)
            .load(uri)
            .fit().centerInside()
            .rotate(90F)
            .error(R.drawable.ic_launcher_foreground)
            .into(binding.imgView);


        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCrop.setOnClickListener {
            CropImage.activity(uri).start(this)
        }

        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, FindingActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri;
                Picasso.with(this)
                    .load(resultUri)
                    .fit().centerInside()
                    .error(R.drawable.ic_launcher_foreground)
                    .into(binding.imgView);
            }
        }
    }
}