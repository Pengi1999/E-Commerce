package com.nhathao.e_commerce.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.RangeSlider.OnChangeListener
import com.google.android.material.slider.RangeSlider.OnSliderTouchListener
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivityFilterBinding

private lateinit var binding: ActivityFilterBinding
class FilterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val valueFrom = intent.getFloatExtra("valueFrom", 0f)
        val valueTo = intent.getFloatExtra("valueTo", 0f)

        binding.txtValueFrom.text = "$${valueFrom.toInt()}"
        binding.txtValueTo.text = "$${valueTo.toInt()}"
        binding.rangeSliderPrice.valueFrom = valueFrom
        binding.rangeSliderPrice.valueTo = valueTo
        binding.rangeSliderPrice.setValues(valueFrom,valueTo)
        binding.rangeSliderPrice.isTickVisible = false

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnDiscard.setOnClickListener {
            finish()
        }

        binding.btnApply.setOnClickListener {
            val data = Intent()
            data.putExtra("ValueFrom", binding.rangeSliderPrice.values[0].toInt())
            data.putExtra("ValueTo", binding.rangeSliderPrice.values[1].toInt())
            setResult(Activity.RESULT_OK, data)
            finish()
        }

        binding.rangeSliderPrice.addOnChangeListener(object : OnChangeListener{
            override fun onValueChange(slider: RangeSlider, value: Float, fromUser: Boolean) {
                binding.txtValueFrom.text = "$${slider.values[0].toInt()}"
                binding.txtValueTo.text = "$${slider.values[1].toInt()}"
            }
        })
    }
}