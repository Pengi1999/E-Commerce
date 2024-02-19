package com.nhathao.e_commerce.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivityEditingShippingAddressBinding
import com.nhathao.e_commerce.models.ShippingAddress
import com.nhathao.e_commerce.models.User

private lateinit var binding: ActivityEditingShippingAddressBinding
class EditingShippingAddressActivity : AppCompatActivity() {
    private lateinit var dialog: BottomSheetDialog
    private lateinit var dbRefShippingAddress: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditingShippingAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRefShippingAddress = FirebaseDatabase.getInstance().getReference("ShippingAddresses")

        val bundleGet = intent.extras
        val shippingAddress = bundleGet?.getSerializable("shippingAddress") as ShippingAddress

        binding.layoutEdtFullName.editText?.setText(shippingAddress.fullName)
        binding.layoutEdtAddress.editText?.setText(shippingAddress.address)
        binding.layoutEdtCity.editText?.setText(shippingAddress.city)
        binding.layoutEdtRegion.editText?.setText(shippingAddress.region)
        binding.layoutEdtZipCode.editText?.setText(shippingAddress.zipCode)
        binding.layoutEdtCountry.editText?.setText(shippingAddress.country)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnRemove.setOnClickListener {
            shippingAddress.used = false
            shippingAddress.status = false
            dbRefShippingAddress.child(shippingAddress.shippingAddressId).setValue(shippingAddress)
            finish()
        }

        binding.layoutEdtCountry.editText?.setOnClickListener {
            showBottomSheetChooseCountry()
        }

        binding.btnSaveAddress.setOnClickListener {
            val isNotEmpty =
                checkEmpty(binding.layoutEdtFullName.editText?.text.toString(),
                    binding.layoutEdtAddress.editText?.text.toString(),
                    binding.layoutEdtCity.editText?.text.toString(),
                    binding.layoutEdtRegion.editText?.text.toString(),
                    binding.layoutEdtZipCode.editText?.text.toString(),
                    binding.layoutEdtCountry.editText?.text.toString())
            if (isNotEmpty) {
                shippingAddress.fullName = binding.layoutEdtFullName.editText?.text.toString()
                shippingAddress.address = binding.layoutEdtAddress.editText?.text.toString()
                shippingAddress.city = binding.layoutEdtCity.editText?.text.toString()
                shippingAddress.region = binding.layoutEdtRegion.editText?.text.toString()
                shippingAddress.zipCode = binding.layoutEdtZipCode.editText?.text.toString()
                shippingAddress.country = binding.layoutEdtCountry.editText?.text.toString()
                dbRefShippingAddress.child(shippingAddress.shippingAddressId).setValue(shippingAddress)
                finish()
            }
        }

        binding.layoutEdtFullName.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtFullName.error = ""
        }

        binding.layoutEdtAddress.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtAddress.error = ""
        }

        binding.layoutEdtCity.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtCity.error = ""
        }

        binding.layoutEdtRegion.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtRegion.error = ""
        }

        binding.layoutEdtZipCode.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtZipCode.error = ""
        }

        binding.layoutEdtCountry.editText?.doOnTextChanged { text, start, before, count ->
            binding.layoutEdtCountry.error = ""
        }
    }

    private fun checkEmpty(fullName: String, address: String, city: String, region: String, zipCode: String, country: String): Boolean {
        var isNotEmpty = true
        if (fullName.isEmpty()) {
            isNotEmpty = false
            binding.layoutEdtFullName.error = "Field can't be empty"
        } else {
            binding.layoutEdtFullName.error = ""
        }
        if (address.isEmpty()) {
            isNotEmpty = false
            binding.layoutEdtAddress.error = "Field can't be empty"
        } else {
            binding.layoutEdtAddress.error = ""
        }
        if (city.isEmpty()) {
            isNotEmpty = false
            binding.layoutEdtCity.error = "Field can't be empty"
        } else {
            binding.layoutEdtCity.error = ""
        }
        if (region.isEmpty()) {
            isNotEmpty = false
            binding.layoutEdtRegion.error = "Field can't be empty"
        } else {
            binding.layoutEdtRegion.error = ""
        }
        if (zipCode.isEmpty()) {
            isNotEmpty = false
            binding.layoutEdtZipCode.error = "Field can't be empty"
        } else {
            binding.layoutEdtZipCode.error = ""
        }
        if (country.isEmpty()) {
            isNotEmpty = false
            binding.layoutEdtCountry.error = "Field can't be empty"
        } else {
            binding.layoutEdtCountry.error = ""
        }
        return isNotEmpty
    }

    private fun showBottomSheetChooseCountry() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_select_country, null)
        dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)
        val searchViewCountry = dialogView.findViewById<SearchView>(R.id.searchViewCountry)
        val lvCountry = dialogView.findViewById<ListView>(R.id.lvCountry)

        val arrQuocGiaFull = resources.getStringArray(R.array.arrQuocGia)
        val arrQuocGia = mutableListOf<String>()
        arrQuocGia.addAll(arrQuocGiaFull)
        val arrayQuocGiaAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,arrQuocGia)
        lvCountry.adapter = arrayQuocGiaAdapter

        searchViewCountry.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                arrQuocGia.clear()
                if (newText != null)
                    arrQuocGia.addAll(arrQuocGiaFull.filter { it.contains(newText, true)})
                else
                    arrQuocGia.addAll(arrQuocGiaFull)
                arrayQuocGiaAdapter.notifyDataSetChanged()
                return false
            }
        })

        lvCountry.setOnItemClickListener { parent, view, position, id ->
            binding.layoutEdtCountry.editText?.setText(arrQuocGia[position])
            dialog.dismiss()
        }

        dialog.show()
    }
}