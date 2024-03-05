package com.nhathao.e_commerce.activities

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.RangeSlider.OnChangeListener
import com.google.android.material.slider.RangeSlider.OnSliderTouchListener
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivityFilterBinding

private lateinit var binding: ActivityFilterBinding

class FilterActivity : AppCompatActivity() {
    private var valueFrom: Float = 0f
    private var valueTo: Float = 0f
    private var isColorBlack: Boolean = false
    private var isColorWhite: Boolean = false
    private var isColorRed: Boolean = false
    private var isColorGray: Boolean = false
    private var isColorOrange: Boolean = false
    private var isColorBlue: Boolean = false
    private var isSizeXS: Boolean = false
    private var isSizeS: Boolean = false
    private var isSizeM: Boolean = false
    private var isSizeL: Boolean = false
    private var isSizeXL: Boolean = false
    private var requestCodeFilter = 2
    private lateinit var sizes: ArrayList<String>
    private lateinit var colors: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundleGet = intent.extras
        if (bundleGet != null) {
            valueFrom = bundleGet.getFloat("valueFrom")
            valueTo = bundleGet.getFloat("valueTo")
        }

        binding.txtValueFrom.text = "$${valueFrom.toInt()}"
        binding.txtValueTo.text = "$${valueTo.toInt()}"
        binding.rangeSliderPrice.valueFrom = valueFrom
        binding.rangeSliderPrice.valueTo = valueTo
        binding.rangeSliderPrice.setValues(valueFrom, valueTo)
        binding.rangeSliderPrice.isTickVisible = false

        sizes = arrayListOf()
        colors = arrayListOf()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.blockBlackColor.setOnClickListener {
            if (!isColorBlack) {
                binding.outlineBlackSelected.visibility = View.VISIBLE
                isColorBlack = true
                colors.add("Black")
            } else {
                binding.outlineBlackSelected.visibility = View.INVISIBLE
                isColorBlack = false
                colors.remove("Black")
            }
        }

        binding.blockWhiteColor.setOnClickListener {
            if (!isColorWhite) {
                binding.outlineWhiteSelected.visibility = View.VISIBLE
                isColorWhite = true
                colors.add("White")
            } else {
                binding.outlineWhiteSelected.visibility = View.INVISIBLE
                isColorWhite = false
                colors.remove("White")
            }
        }

        binding.blockRedColor.setOnClickListener {
            if (!isColorRed) {
                binding.outlineRedSelected.visibility = View.VISIBLE
                isColorRed = true
                colors.add("Red")
            } else {
                binding.outlineRedSelected.visibility = View.INVISIBLE
                isColorRed = false
                colors.remove("Red")
            }
        }

        binding.blockGrayColor.setOnClickListener {
            if (!isColorGray) {
                binding.outlineGraySelected.visibility = View.VISIBLE
                isColorGray = true
                colors.add("Gray")
            } else {
                binding.outlineGraySelected.visibility = View.INVISIBLE
                isColorGray = false
                colors.remove("Gray")
            }
        }

        binding.blockOrangeColor.setOnClickListener {
            if (!isColorOrange) {
                binding.outlineOrangeSelected.visibility = View.VISIBLE
                isColorOrange = true
                colors.add("Orange")
            } else {
                binding.outlineOrangeSelected.visibility = View.INVISIBLE
                isColorOrange = false
                colors.remove("Orange")
            }
        }

        binding.blockBlueColor.setOnClickListener {
            if (!isColorBlue) {
                binding.outlineBlueSelected.visibility = View.VISIBLE
                isColorBlue = true
                colors.add("Blue")
            } else {
                binding.outlineBlueSelected.visibility = View.INVISIBLE
                isColorBlue = false
                colors.remove("Blue")
            }
        }

        binding.txtSizeXS.setOnClickListener {
            if (!isSizeXS) {
                binding.txtSizeXS.setBackgroundResource(R.drawable.bg_size_selected)
                binding.txtSizeXS.setTextColor(resources.getColor(R.color.white))
                isSizeXS = true
                sizes.add("XS")
            } else {
                binding.txtSizeXS.setBackgroundResource(R.drawable.bg_size)
                binding.txtSizeXS.setTextColor(resources.getColor(R.color.black_custom))
                isSizeXS = false
                sizes.remove("XS")
            }
        }

        binding.txtSizeS.setOnClickListener {
            if (!isSizeS) {
                binding.txtSizeS.setBackgroundResource(R.drawable.bg_size_selected)
                binding.txtSizeS.setTextColor(resources.getColor(R.color.white))
                isSizeS = true
                sizes.add("S")
            } else {
                binding.txtSizeS.setBackgroundResource(R.drawable.bg_size)
                binding.txtSizeS.setTextColor(resources.getColor(R.color.black_custom))
                isSizeS = false
                sizes.remove("S")
            }
        }

        binding.txtSizeM.setOnClickListener {
            if (!isSizeM) {
                binding.txtSizeM.setBackgroundResource(R.drawable.bg_size_selected)
                binding.txtSizeM.setTextColor(resources.getColor(R.color.white))
                isSizeM = true
                sizes.add("M")
            } else {
                binding.txtSizeM.setBackgroundResource(R.drawable.bg_size)
                binding.txtSizeM.setTextColor(resources.getColor(R.color.black_custom))
                isSizeM = false
                sizes.remove("M")
            }
        }

        binding.txtSizeL.setOnClickListener {
            if (!isSizeL) {
                binding.txtSizeL.setBackgroundResource(R.drawable.bg_size_selected)
                binding.txtSizeL.setTextColor(resources.getColor(R.color.white))
                isSizeL = true
                sizes.add("L")
            } else {
                binding.txtSizeL.setBackgroundResource(R.drawable.bg_size)
                binding.txtSizeL.setTextColor(resources.getColor(R.color.black_custom))
                isSizeL = false
                sizes.remove("L")
            }
        }

        binding.txtSizeXL.setOnClickListener {
            if (!isSizeXL) {
                binding.txtSizeXL.setBackgroundResource(R.drawable.bg_size_selected)
                binding.txtSizeXL.setTextColor(resources.getColor(R.color.white))
                isSizeXL = true
                sizes.add("XL")
            } else {
                binding.txtSizeXL.setBackgroundResource(R.drawable.bg_size)
                binding.txtSizeXL.setTextColor(resources.getColor(R.color.black_custom))
                isSizeXL = false
                sizes.remove("XL")
            }
        }

        binding.blockBrand.setOnClickListener {
            Toast.makeText(this, "Do it later", Toast.LENGTH_SHORT).show()
        }

        binding.btnDiscard.setOnClickListener {
            finish()
        }

        binding.btnApply.setOnClickListener {
            val data = Intent()
            val bundlePassing = Bundle()
            bundlePassing.putInt("requestCode", requestCodeFilter)
            bundlePassing.putInt("ValueFrom", binding.rangeSliderPrice.values[0].toInt())
            bundlePassing.putInt("ValueTo", binding.rangeSliderPrice.values[1].toInt())
            bundlePassing.putStringArrayList("sizes", sizes)
            bundlePassing.putStringArrayList("colors", colors)
            data.putExtras(bundlePassing)
            setResult(Activity.RESULT_OK, data)
            finish()
        }

        binding.rangeSliderPrice.addOnChangeListener(object : OnChangeListener {
            override fun onValueChange(slider: RangeSlider, value: Float, fromUser: Boolean) {
                binding.txtValueFrom.text = "$${slider.values[0].toInt()}"
                binding.txtValueTo.text = "$${slider.values[1].toInt()}"
            }
        })
    }
}