package com.nhathao.e_commerce.activities

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivityBrandSelectBinding

private lateinit var binding: ActivityBrandSelectBinding

class BrandSelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrandSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.chkAdidas.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.txtAdidas.setTextColor(resources.getColor(R.color.red_button))
                binding.txtAdidas.typeface = Typeface.DEFAULT_BOLD
            } else {
                binding.txtAdidas.setTextColor(resources.getColor(R.color.black_custom))
                binding.txtAdidas.typeface = Typeface.DEFAULT
            }
        }

        binding.chkAdidasOrigin.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.txtAdidasOrigin.setTextColor(resources.getColor(R.color.red_button))
                binding.txtAdidasOrigin.typeface = Typeface.DEFAULT_BOLD
            } else {
                binding.txtAdidasOrigin.setTextColor(resources.getColor(R.color.black_custom))
                binding.txtAdidasOrigin.typeface = Typeface.DEFAULT
            }
        }

        binding.chkBlend.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.txtBlend.setTextColor(resources.getColor(R.color.red_button))
                binding.txtBlend.typeface = Typeface.DEFAULT_BOLD
            } else {
                binding.txtBlend.setTextColor(resources.getColor(R.color.black_custom))
                binding.txtBlend.typeface = Typeface.DEFAULT
            }
        }

        binding.chkBoutique.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.txtBoutique.setTextColor(resources.getColor(R.color.red_button))
                binding.txtBoutique.typeface = Typeface.DEFAULT_BOLD
            } else {
                binding.txtBoutique.setTextColor(resources.getColor(R.color.black_custom))
                binding.txtBoutique.typeface = Typeface.DEFAULT
            }
        }

        binding.chkChampion.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.txtChampion.setTextColor(resources.getColor(R.color.red_button))
                binding.txtChampion.typeface = Typeface.DEFAULT_BOLD
            } else {
                binding.txtChampion.setTextColor(resources.getColor(R.color.black_custom))
                binding.txtChampion.typeface = Typeface.DEFAULT
            }
        }

        binding.chkDiesel.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.txtDiesel.setTextColor(resources.getColor(R.color.red_button))
                binding.txtDiesel.typeface = Typeface.DEFAULT_BOLD
            } else {
                binding.txtDiesel.setTextColor(resources.getColor(R.color.black_custom))
                binding.txtDiesel.typeface = Typeface.DEFAULT
            }
        }

        binding.chkJJ.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.txtJJ.setTextColor(resources.getColor(R.color.red_button))
                binding.txtJJ.typeface = Typeface.DEFAULT_BOLD
            } else {
                binding.txtJJ.setTextColor(resources.getColor(R.color.black_custom))
                binding.txtJJ.typeface = Typeface.DEFAULT
            }
        }

        binding.chkNaf.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.txtNaf.setTextColor(resources.getColor(R.color.red_button))
                binding.txtNaf.typeface = Typeface.DEFAULT_BOLD
            } else {
                binding.txtNaf.setTextColor(resources.getColor(R.color.black_custom))
                binding.txtNaf.typeface = Typeface.DEFAULT
            }
        }

        binding.chkValentino.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.txtValentino.setTextColor(resources.getColor(R.color.red_button))
                binding.txtValentino.typeface = Typeface.DEFAULT_BOLD
            } else {
                binding.txtValentino.setTextColor(resources.getColor(R.color.black_custom))
                binding.txtValentino.typeface = Typeface.DEFAULT
            }
        }

        binding.chkOliver.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.txtOliver.setTextColor(resources.getColor(R.color.red_button))
                binding.txtOliver.typeface = Typeface.DEFAULT_BOLD
            } else {
                binding.txtOliver.setTextColor(resources.getColor(R.color.black_custom))
                binding.txtOliver.typeface = Typeface.DEFAULT
            }
        }

        binding.btnDiscard.setOnClickListener {
            finish()
        }

        binding.btnApply.setOnClickListener {
            finish()
        }
    }
}