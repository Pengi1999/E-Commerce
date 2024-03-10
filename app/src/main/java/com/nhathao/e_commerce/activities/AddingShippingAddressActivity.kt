package com.nhathao.e_commerce.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.adapters.RvAdapterShippingAddress
import com.nhathao.e_commerce.databinding.ActivityAddingShippingAddressBinding
import com.nhathao.e_commerce.models.Rating
import com.nhathao.e_commerce.models.ShippingAddress
import com.nhathao.e_commerce.models.User

private lateinit var binding: ActivityAddingShippingAddressBinding
class AddingShippingAddressActivity : AppCompatActivity() {
    private lateinit var dialog: BottomSheetDialog
    private lateinit var dbRefShippingAddress: DatabaseReference
    private lateinit var dsShippingAddressOfUser: MutableList<ShippingAddress>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddingShippingAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRefShippingAddress = FirebaseDatabase.getInstance().getReference("ShippingAddresses")

        val bundleGet = intent.extras
        val user = bundleGet?.getSerializable("user") as User

        dsShippingAddressOfUser = mutableListOf()

        dbRefShippingAddress.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dsShippingAddressOfUser.clear()
                if (snapshot.exists()) {
                    for (shippingAddressSnap in snapshot.children) {
                        if (shippingAddressSnap.child("userAccountName").value.toString() == user.userAccountName){
                            val shippingAddress = ShippingAddress(
                                shippingAddressSnap.child("shippingAddressId").value.toString(),
                                shippingAddressSnap.child("userAccountName").value.toString(),
                                shippingAddressSnap.child("fullName").value.toString(),
                                shippingAddressSnap.child("address").value.toString(),
                                shippingAddressSnap.child("city").value.toString(),
                                shippingAddressSnap.child("region").value.toString(),
                                shippingAddressSnap.child("zipCode").value.toString(),
                                shippingAddressSnap.child("country").value.toString(),
                                shippingAddressSnap.child("used").value.toString().toBoolean(),
                                shippingAddressSnap.child("status").value.toString().toBoolean()
                            )
                            dsShippingAddressOfUser.add(shippingAddress)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        binding.btnBack.setOnClickListener {
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
                addShippingAddress(user)
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

    private fun addShippingAddress(user: User) {
        for (shippingAddressChildren in dsShippingAddressOfUser){
            if(shippingAddressChildren.used) {
                dbRefShippingAddress.child(shippingAddressChildren.shippingAddressId).child("used").setValue(false)
                break
            }
        }
        val shippingAddress = ShippingAddress(
            "",
            user.userAccountName!!,
            binding.layoutEdtFullName.editText?.text.toString(),
            binding.layoutEdtAddress.editText?.text.toString(),
            binding.layoutEdtCity.editText?.text.toString(),
            binding.layoutEdtRegion.editText?.text.toString(),
            binding.layoutEdtZipCode.editText?.text.toString(),
            binding.layoutEdtCountry.editText?.text.toString(),
            used = true,
            status = true
        )
        shippingAddress.shippingAddressId = dbRefShippingAddress.push().key.toString()
        dbRefShippingAddress.child(shippingAddress.shippingAddressId).setValue(shippingAddress)
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

        searchViewCountry.setOnQueryTextListener(object : OnQueryTextListener{
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