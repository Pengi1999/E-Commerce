package com.nhathao.e_commerce.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.appSettingOpen
import com.nhathao.e_commerce.warningPermissionDialog
import com.nhathao.e_commerce.databinding.ActivityTakingAphotoBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

class TakingAPhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTakingAphotoBinding
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var camera: Camera
    private lateinit var cameraSelector: CameraSelector
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private val multiplePermissionId = 14
    private val multiplePermissionNameList = if(Build.VERSION.SDK_INT >= 33) {
        arrayListOf(
            android.Manifest.permission.CAMERA,
        )
    } else {
        arrayListOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTakingAphotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkMultiplePermission()){
            startCamera()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnFlashToggle.setOnClickListener {
            setFlashIcon(camera)
        }

        binding.btnFlipCamera.setOnClickListener {
            when (lensFacing){
                CameraSelector.LENS_FACING_FRONT -> lensFacing = CameraSelector.LENS_FACING_BACK
                CameraSelector.LENS_FACING_BACK -> lensFacing = CameraSelector.LENS_FACING_FRONT
            }
            bindCameraUserCase()
        }

        binding.btnCapture.setOnClickListener {
            takePhoto()
        }
    }
    private fun checkMultiplePermission(): Boolean {
        val listPermissionNeeded = arrayListOf<String>()
        for (permission in multiplePermissionNameList) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(permission)
            }
        }
        if (listPermissionNeeded.isNotEmpty()){
            ActivityCompat.requestPermissions(
                this,
                listPermissionNeeded.toTypedArray(),
                multiplePermissionId
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == multiplePermissionId) {
            if (grantResults.isNotEmpty()){
                var isGrant = true
                for (element in grantResults) {
                    if (element == PackageManager.PERMISSION_DENIED) {
                        isGrant = false
                    }
                }
                if (isGrant){
                    // here all permissions granted successfully
                    startCamera()
                } else {
                    var someDenied = false
                    for (permission in permissions){
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                permission
                            )){
                            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED){
                                someDenied = true
                            }
                        }
                    }
                    if (someDenied) {
                        // here app Setting open because all permission is not granted
                        // and permanent denied
                        appSettingOpen(this)
                    } else {
                        // here warning permission show
                        warningPermissionDialog(this){_:DialogInterface, which:Int ->
                            when(which){
                                DialogInterface.BUTTON_POSITIVE -> checkMultiplePermission()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUserCase()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = maxOf(width, height).toDouble() / minOf(width, height)
        return if (abs(previewRatio - 4.0 / 3.0) <= abs(previewRatio - 16.0 / 9.0)) {
            AspectRatio.RATIO_4_3
        } else {
            AspectRatio.RATIO_16_9
        }
    }

    private fun bindCameraUserCase() {
        val screenAspectRatio = aspectRatio(
            binding.previewView.width,
            binding.previewView.height
        )
        val rotation = binding.previewView.display.rotation

        val resolutionSelector = ResolutionSelector.Builder()
            .setAspectRatioStrategy(
                AspectRatioStrategy(
                    screenAspectRatio,
                    AspectRatioStrategy.FALLBACK_RULE_AUTO
                )
            )
            .build()

        val preview = Preview.Builder()
            .setResolutionSelector(resolutionSelector)
            .setTargetRotation(rotation)
            .build()
            .also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setResolutionSelector(resolutionSelector)
            .setTargetRotation(rotation)
            .build()

        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        try {
            cameraProvider.unbindAll()

            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    private fun setFlashIcon(camera: Camera) {
        if (camera.cameraInfo.hasFlashUnit()){
            if (camera.cameraInfo.torchState.value == 0) {
                camera.cameraControl.enableTorch(true)
                binding.btnFlashToggle.setImageResource(R.drawable.ic_flash_off_black)
            }
            else {
                camera.cameraControl.enableTorch(false)
                binding.btnFlashToggle.setImageResource(R.drawable.ic_flash_on_black)
            }
        }
        else {
            Toast.makeText(this, "Flash Is Not Available", Toast.LENGTH_LONG).show()
            binding.btnFlashToggle.isEnabled = false
        }
    }

    private fun takePhoto() {
        val imageFolder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Images"
        )
        if (!imageFolder.exists()){
            imageFolder.mkdir()
        }

        val fileName = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(System.currentTimeMillis()) + ".jpg"
        val imageFile = File(imageFolder, fileName)
        val outputOption = OutputFileOptions.Builder(imageFile).build()

        imageCapture.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val intent = Intent(this@TakingAPhotoActivity, CropImageActivity::class.java)
                    intent.putExtra("imageFile", imageFile)
                    startActivity(intent)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@TakingAPhotoActivity, exception.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        )
    }
}