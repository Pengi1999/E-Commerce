package com.nhathao.e_commerce.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.databinding.ActivityAddingShippingAddressBinding

private lateinit var binding: ActivityAddingShippingAddressBinding
class AddingShippingAddressActivity : AppCompatActivity() {
    private lateinit var dialog: BottomSheetDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddingShippingAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layoutEdtCountry.editText?.setOnClickListener {
            showBottomSheetChooseCountry()
        }
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