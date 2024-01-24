package com.nhathao.e_commerce.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nhathao.e_commerce.Interfaces.RvCategoryInterface
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.activities.FilterActivity
import com.nhathao.e_commerce.adapters.RvAdapterCategory
import com.nhathao.e_commerce.adapters.RvAdapterProduct
import com.nhathao.e_commerce.models.Product
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CatalogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CatalogFragment : Fragment() {
    private lateinit var dialog: BottomSheetDialog
    private lateinit var txtSortByPopular: TextView
    private lateinit var txtSortByNewest: TextView
    private lateinit var txtSortByReview: TextView
    private lateinit var txtSortByPriceLowest: TextView
    private lateinit var txtSortByPriceHighest: TextView
    private lateinit var dbRef: DatabaseReference
    private lateinit var btnBack: ImageButton
    private lateinit var txtCategoryActionBar: TextView
    private lateinit var btnStyleShowList: ImageButton
    private lateinit var blockFilters: LinearLayout
    private lateinit var blockSort: LinearLayout
    private lateinit var txtViewSort: TextView
    private lateinit var txtCategory: TextView
    private lateinit var rcvCategory: RecyclerView
    private lateinit var dsCategory: MutableList<String>
    private lateinit var dsProduct: MutableList<Product>
    private lateinit var dsProductAll: MutableList<Product>
    private lateinit var dsProductByCategory: MutableList<Product>
    private lateinit var dsProductTops: MutableList<Product>
    private lateinit var rcvProduct: RecyclerView
    private lateinit var adapterCategory:RvAdapterCategory
    private lateinit var adapterProduct:RvAdapterProduct
    private var isViewList: Boolean = true
    private var sortMode: String = "Ascending"
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)

        dbRef = FirebaseDatabase.getInstance().getReference("Products")

        btnBack = view.findViewById(R.id.btnBack)
        txtCategoryActionBar = view.findViewById(R.id.txtCategoryActionBar)
        btnStyleShowList = view.findViewById(R.id.btnStyleShowList)
        blockFilters = view.findViewById(R.id.blockFilters)
        blockSort = view.findViewById(R.id.blockSort)
        txtViewSort = view.findViewById(R.id.txtViewSort)
        txtCategory = view.findViewById(R.id.txtCategory)
        rcvCategory = view.findViewById(R.id.rcvCategory)
        rcvProduct = view.findViewById(R.id.rcvProduct)

        val sex = this.activity?.intent?.getStringExtra("Sex")
        val type = this.activity?.intent?.getStringExtra("Type")
        val category = this.activity?.intent?.getStringExtra("Category")

        txtCategory.setText("$sex's $category")

        dsCategory = mutableListOf<String>()
        dsProduct = mutableListOf<Product>()
        dsProductAll = mutableListOf<Product>()
        dsProductByCategory = mutableListOf<Product>()
        dsProductTops = mutableListOf<Product>()

        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                dsProduct.clear()
                dsCategory.clear()
                dsProductAll.clear()
                dsProductTops.clear()
                dsProductByCategory.clear()
                if (snapshot.exists()) {
                    if (type == "Clothes") {
                        dsCategory.add("All")
                        dsCategory.add("Tops")
                    }
                    for (productSnap in snapshot.children) {
                        if (productSnap.child("productSex").value.toString() == sex &&
                            productSnap.child("productType").value.toString() == type
                        ) {
                            val productData = Product(
                                productSnap.child("productId").value.toString(),
                                productSnap.child("productName").value.toString(),
                                productSnap.child("productDescribe").value.toString(),
                                productSnap.child("productImage").value.toString(),
                                productSnap.child("productSex").value.toString(),
                                productSnap.child("productType").value.toString(),
                                productSnap.child("productCategory").value.toString(),
                                productSnap.child("productBrand").value.toString(),
                                productSnap.child("productMode").value.toString(),
                                productSnap.child("productPrice").value.toString().toInt(),
                                productSnap.child("productRating").value.toString().toFloat(),
                                productSnap.child("productRatingQuantity").value.toString().toInt(),
                            )
                            dsProductAll.add(productData)

                            if (productData.productCategory == "Shirt" ||
                                productData.productCategory == "Blouse" ||
                                productData.productCategory == "T-shirt"
                            ) {
                                dsProductTops.add(productData)
                            }
                            if (productData.productCategory == category) {
                                dsProductByCategory.add(productData)
                            }
                            if (!dsCategory.contains(productSnap.child("productCategory").value.toString())) {
                                dsCategory.add(productSnap.child("productCategory").value.toString())
                            }
                        }
                    }
                    if(category == "All")
                        dsProduct.addAll(dsProductAll)
                    else if (category == "Tops")
                        dsProduct.addAll(dsProductTops)
                    else
                        dsProduct.addAll(dsProductByCategory)

                    dsProduct.sortBy { it.productPrice }
                    hienProductViewList()

                    adapterCategory = RvAdapterCategory(dsCategory, object :RvCategoryInterface{
                        override fun OnClickCategory(pos: Int) {
                            txtCategory.setText("$sex's ${dsCategory[pos]}")
                            xuLyClickCategory(dsCategory[pos])
                        }
                    })

                    rcvCategory.adapter = adapterCategory
                    rcvCategory.layoutManager = LinearLayoutManager(
                        this@CatalogFragment.requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        btnBack.setOnClickListener {
            this.activity?.supportFragmentManager?.popBackStack()
        }

        btnStyleShowList.setOnClickListener {
            xuLyBoCucHienProduct()
        }

        blockFilters.setOnClickListener {
            val intent = Intent(requireContext(), FilterActivity::class.java)
            val dsProDuctAllClone = dsProductAll.sortedBy { it.productPrice }
            val valueFrom = dsProDuctAllClone.first().productPrice.toFloat()
            val valueTo = dsProDuctAllClone.last().productPrice.toFloat()
            intent.putExtra("valueFrom", valueFrom)
            intent.putExtra("valueTo", valueTo)
            startForResult.launch(intent)
        }

        blockSort.setOnClickListener {
            showBottomSheetSort()
        }
        return view
    }

    private fun showBottomSheetSort() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_sort, null)
        dialog = BottomSheetDialog(this.requireContext())
        dialog.setContentView(dialogView)
        txtSortByPopular = dialogView.findViewById<TextView>(R.id.txtSortByPopular)
        txtSortByNewest = dialogView.findViewById<TextView>(R.id.txtSortByNewest)
        txtSortByReview = dialogView.findViewById<TextView>(R.id.txtSortByReview)
        txtSortByPriceLowest = dialogView.findViewById<TextView>(R.id.txtSortByPriceLowest)
        txtSortByPriceHighest = dialogView.findViewById<TextView>(R.id.txtSortByPriceHighest)

        txtSortByPopular.setOnClickListener {
            txtViewSort.text = txtSortByPopular .text
            dialog.dismiss()
        }

        txtSortByNewest.setOnClickListener {
            txtViewSort.text = txtSortByNewest.text
            dialog.dismiss()
        }

        txtSortByReview.setOnClickListener {
            txtViewSort.text = txtSortByReview.text
            dialog.dismiss()
        }

        txtSortByPriceLowest.setOnClickListener {
            dsProduct.sortBy { it.productPrice }
            if (isViewList) {
                hienProductViewList()
            }
            else {
                hienProductViewMode()
            }
            txtViewSort.text = txtSortByPriceLowest.text
            sortMode = "Ascending"
            dialog.dismiss()
        }

        txtSortByPriceHighest.setOnClickListener {
            dsProduct.sortByDescending { it.productPrice }
            if (isViewList) {
                hienProductViewList()
            }
            else {
                hienProductViewMode()
            }
            txtViewSort.text = txtSortByPriceHighest.text
            sortMode = "Descending"
            dialog.dismiss()
        }

        eventOnTouchForBottomSheetSort(txtSortByPopular)
        eventOnTouchForBottomSheetSort(txtSortByNewest)
        eventOnTouchForBottomSheetSort(txtSortByReview)
        eventOnTouchForBottomSheetSort(txtSortByPriceLowest)
        eventOnTouchForBottomSheetSort(txtSortByPriceHighest)

        dialog.show()
    }

    private fun eventOnTouchForBottomSheetSort(txtView: TextView?) {
        txtView!!.setOnTouchListener(object :OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    txtView.setBackgroundResource(R.color.red_button)
                    txtView.setTextColor(resources.getColor(R.color.white))
                }
                else if (event?.action == MotionEvent.ACTION_UP) {
                    txtView.setBackgroundResource(R.color.background_default)
                    txtView.setTextColor(resources.getColor(R.color.black_custom))
                }
                return false
            }
        })
    }

    private fun xuLyBoCucHienProduct() {
        if (isViewList) {
            txtCategoryActionBar.text = txtCategory.text
            txtCategoryActionBar.visibility = View.VISIBLE
            txtCategory.visibility = View.GONE
            btnStyleShowList.setBackgroundResource(R.drawable.ic_view_list)
            isViewList = false
            hienProductViewMode()
        }
        else {
            txtCategoryActionBar.visibility = View.INVISIBLE
            txtCategory.visibility = View.VISIBLE
            btnStyleShowList.setBackgroundResource(R.drawable.ic_view_module)
            isViewList = true
            hienProductViewList()
        }
    }

    private fun hienProductViewMode() {
        adapterProduct = RvAdapterProduct(dsProduct, R.layout.layout_item)
        rcvProduct.adapter = adapterProduct
        rcvProduct.layoutManager = GridLayoutManager(
            this.requireContext(),
            2,
            GridLayoutManager.VERTICAL,
            false
        )
    }

    private fun hienProductViewList() {
        adapterProduct = RvAdapterProduct(dsProduct, R.layout.layout_item2)
        rcvProduct.adapter = adapterProduct
        rcvProduct.layoutManager = LinearLayoutManager(
            this.requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun xuLyClickCategory(categorySelected: String) {
        dsProduct.clear()
        if (categorySelected == "All")
            dsProduct.addAll(dsProductAll)
        else if (categorySelected == "Tops")
            dsProduct.addAll(dsProductTops)
        else {
            dsProductByCategory.clear()
            for (product in dsProductAll){
                if (product.productCategory == categorySelected)
                    dsProductByCategory.add(product)
            }
            dsProduct.addAll(dsProductByCategory)
        }
        dsProduct.sortBy { it.productPrice }
        sortMode = "Ascending"
        txtViewSort.text = "Price: lowest to high"
        if (isViewList) {
            hienProductViewList()
        }
        else {
            hienProductViewMode()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CatalogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CatalogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val valueFrom = intent?.getIntExtra("ValueFrom", 0)
            val valueTo = intent?.getIntExtra("ValueTo", 0)
            dsProduct.clear()
            dsProduct.addAll(dsProductAll)
            dsProduct.removeIf {it.productPrice < valueFrom!! || it.productPrice > valueTo!!}
            if (sortMode == "Ascending")
                dsProduct.sortBy { it.productPrice }
            else if (sortMode == "Descending")
                dsProduct.sortByDescending { it.productPrice }
            if (isViewList) {
                hienProductViewList()
            }
            else {
                hienProductViewMode()
            }
        }
    }
}