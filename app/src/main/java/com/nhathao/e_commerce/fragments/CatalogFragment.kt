package com.nhathao.e_commerce.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nhathao.e_commerce.Interfaces.EventCategoryItemListening
import com.nhathao.e_commerce.Interfaces.EventProductItemListening
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.activities.FilterActivity
import com.nhathao.e_commerce.activities.LoginActivity
import com.nhathao.e_commerce.activities.ProductDetailActivity
import com.nhathao.e_commerce.adapters.RvAdapterCategory
import com.nhathao.e_commerce.adapters.RvAdapterProduct
import com.nhathao.e_commerce.models.Bag
import com.nhathao.e_commerce.models.Product
import com.nhathao.e_commerce.models.Quantity
import com.nhathao.e_commerce.models.User

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
    private lateinit var dbRefProduct: DatabaseReference
    private lateinit var dbRefQuantity: DatabaseReference
    private lateinit var dbRefBag: DatabaseReference
    private lateinit var btnBack: ImageButton
    private lateinit var btnSearch: ImageButton
    private lateinit var txtCategoryActionBar: TextView
    private lateinit var btnStyleShowList: ImageButton
    private lateinit var blockFilters: LinearLayout
    private lateinit var blockSort: LinearLayout
    private lateinit var txtViewSort: TextView
    private lateinit var txtCategory: TextView
    private lateinit var rcvCategory: RecyclerView
    private lateinit var dsCategory: MutableList<String>
    private lateinit var dsQuantity: MutableList<Quantity>
    private lateinit var dsProduct: MutableList<Product>
    private lateinit var dsProductAll: MutableList<Product>
    private lateinit var dsProductByCategory: MutableList<Product>
    private lateinit var dsProductTops: MutableList<Product>
    private lateinit var dsBag: MutableList<Bag>
    private lateinit var rcvProduct: RecyclerView
    private lateinit var adapterCategory: RvAdapterCategory
    private lateinit var adapterProduct: RvAdapterProduct
    private lateinit var selectedSize: String
    private lateinit var selectedColor: String
    private var isViewList: Boolean = true
    private var sortMode: String = "Ascending"
    private lateinit var user: User
    private var isLogin: Boolean = false
    private val requestCodeFilter = 2

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

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)

        dbRefProduct = FirebaseDatabase.getInstance().getReference("Products")
        dbRefQuantity = FirebaseDatabase.getInstance().getReference("Quantities")
        dbRefBag = FirebaseDatabase.getInstance().getReference("Bags")

        btnBack = view.findViewById(R.id.btnBack)
        btnSearch = view.findViewById(R.id.btnSearch)
        txtCategoryActionBar = view.findViewById(R.id.txtCategoryActionBar)
        btnStyleShowList = view.findViewById(R.id.btnStyleShowList)
        blockFilters = view.findViewById(R.id.blockFilters)
        blockSort = view.findViewById(R.id.blockSort)
        txtViewSort = view.findViewById(R.id.txtViewSort)
        txtCategory = view.findViewById(R.id.txtCategory)
        rcvCategory = view.findViewById(R.id.rcvCategory)
        rcvProduct = view.findViewById(R.id.rcvProduct)

        val bundleGetData = this.activity?.intent?.extras
        if (bundleGetData != null) {
            isLogin = bundleGetData.getBoolean("isLogin")
            if (isLogin)
                user = bundleGetData.getSerializable("user") as User
        }
        val sex = this.activity?.intent?.getStringExtra("Sex")
        val type = this.activity?.intent?.getStringExtra("Type")
        val category = this.activity?.intent?.getStringExtra("Category")

        txtCategory.text = "$sex's $category"

        dsCategory = mutableListOf()
        dsQuantity = mutableListOf()
        dsProduct = mutableListOf()
        dsProductAll = mutableListOf()
        dsProductByCategory = mutableListOf()
        dsProductTops = mutableListOf()
        dsBag = mutableListOf()

        dbRefProduct.addValueEventListener(object : ValueEventListener {
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
                    if (category == "All")
                        dsProduct.addAll(dsProductAll)
                    else if (category == "Tops")
                        dsProduct.addAll(dsProductTops)
                    else
                        dsProduct.addAll(dsProductByCategory)

                    dsProduct.sortBy { it.productPrice }
                    hienProductViewList()

                    adapterCategory = RvAdapterCategory(dsCategory, object : EventCategoryItemListening {
                        @SuppressLint("SetTextI18n")
                        override fun onClickItemListening(pos: Int) {
                            txtCategory.text = "$sex's ${dsCategory[pos]}"
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
        dbRefQuantity.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dsQuantity.clear()
                if (snapshot.exists()) {
                    for (quantitySnap in snapshot.children) {
                        val quantityData = Quantity(
                            quantitySnap.child("quantityId").value.toString(),
                            quantitySnap.child("productId").value.toString(),
                            quantitySnap.child("productColor").value.toString(),
                            quantitySnap.child("productSize").value.toString(),
                            quantitySnap.child("quantity").value.toString().toInt(),
                        )
                        dsQuantity.add(quantityData)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        dbRefBag.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                dsBag.clear()
                if(snapshot.exists()){
                    for (bagSnap in snapshot.children){
                        val bagData = Bag(
                            bagSnap.child("bagId").value.toString(),
                            bagSnap.child("quantityId").value.toString(),
                            bagSnap.child("userAccountName").value.toString(),
                            bagSnap.child("quantity").value.toString().toInt()
                        )
                        dsBag.add(bagData)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        btnBack.setOnClickListener {
            this.activity?.supportFragmentManager?.popBackStack()
        }

        btnSearch.setOnClickListener {
            Toast.makeText(this.requireContext(), "Do it later", Toast.LENGTH_SHORT).show()
        }

        btnStyleShowList.setOnClickListener {
            xuLyBoCucHienProduct()
        }

        blockFilters.setOnClickListener {
            val intent = Intent(requireContext(), FilterActivity::class.java)
            val dsProDuctAllClone = dsProductAll.sortedBy { it.productPrice }
            val valueFrom = dsProDuctAllClone.first().productPrice.toFloat()
            val valueTo = dsProDuctAllClone.last().productPrice.toFloat()
            val bundlePassing = Bundle()
            bundlePassing.putFloat("valueFrom", valueFrom)
            bundlePassing.putFloat("valueTo", valueTo)
            intent.putExtras(bundlePassing)
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
            txtViewSort.text = txtSortByPopular.text
            Toast.makeText(this.requireContext(), "Do it later", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        txtSortByNewest.setOnClickListener {
            txtViewSort.text = txtSortByNewest.text
            Toast.makeText(this.requireContext(), "Do it later", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        txtSortByReview.setOnClickListener {
            dsProduct.sortByDescending { it.productRating }
            if (isViewList) {
                hienProductViewList()
            } else {
                hienProductViewMode()
            }
            txtViewSort.text = txtSortByReview.text
            sortMode = "Review"
            dialog.dismiss()
        }

        txtSortByPriceLowest.setOnClickListener {
            dsProduct.sortBy { it.productPrice }
            if (isViewList) {
                hienProductViewList()
            } else {
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
            } else {
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

    private fun showBottomSheetSelectColorAndSize(selectedProduct: Product) {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_select_colorandsize, null)
        dialog = BottomSheetDialog(this.requireContext())
        dialog.setContentView(dialogView)

        val blockBlackColor = dialogView.findViewById<RelativeLayout>(R.id.blockBlackColor)
        val blockWhiteColor = dialogView.findViewById<RelativeLayout>(R.id.blockWhiteColor)
        val blockRedColor = dialogView.findViewById<RelativeLayout>(R.id.blockRedColor)
        val blockGrayColor = dialogView.findViewById<RelativeLayout>(R.id.blockGrayColor)
        val blockOrangeColor = dialogView.findViewById<RelativeLayout>(R.id.blockOrangeColor)
        val blockBlueColor = dialogView.findViewById<RelativeLayout>(R.id.blockBlueColor)

        val outlineBlackSelected = dialogView.findViewById<LinearLayout>(R.id.outlineBlackSelected)
        val outlineWhiteSelected = dialogView.findViewById<LinearLayout>(R.id.outlineWhiteSelected)
        val outlineRedSelected = dialogView.findViewById<LinearLayout>(R.id.outlineRedSelected)
        val outlineGraySelected = dialogView.findViewById<LinearLayout>(R.id.outlineGraySelected)
        val outlineOrangeSelected =
            dialogView.findViewById<LinearLayout>(R.id.outlineOrangeSelected)
        val outlineBlueSelected = dialogView.findViewById<LinearLayout>(R.id.outlineBlueSelected)

        val txtSizeXS = dialogView.findViewById<TextView>(R.id.txtSizeXS)
        val txtSizeS = dialogView.findViewById<TextView>(R.id.txtSizeS)
        val txtSizeM = dialogView.findViewById<TextView>(R.id.txtSizeM)
        val txtSizeL = dialogView.findViewById<TextView>(R.id.txtSizeL)
        val txtSizeXL = dialogView.findViewById<TextView>(R.id.txtSizeXL)

        val btnAddToCart = dialogView.findViewById<Button>(R.id.btnAddToCart)

        var isColorBlack: Boolean = false
        var isColorWhite: Boolean = false
        var isColorRed: Boolean = false
        var isColorGray: Boolean = false
        var isColorOrange: Boolean = false
        var isColorBlue: Boolean = false
        var isSizeXS: Boolean = false
        var isSizeS: Boolean = false
        var isSizeM: Boolean = false
        var isSizeL: Boolean = false
        var isSizeXL: Boolean = false

        selectedColor = ""
        selectedSize = ""

        blockBlackColor.setOnClickListener {
            if (!isColorBlack) {
                outlineBlackSelected.visibility = View.VISIBLE
                outlineWhiteSelected.visibility = View.INVISIBLE
                outlineRedSelected.visibility = View.INVISIBLE
                outlineGraySelected.visibility = View.INVISIBLE
                outlineOrangeSelected.visibility = View.INVISIBLE
                outlineBlueSelected.visibility = View.INVISIBLE

                isColorBlack = true
                isColorWhite = false
                isColorRed = false
                isColorGray = false
                isColorOrange = false
                isColorBlue = false

                selectedColor = "Black"
            } else {
                outlineBlackSelected.visibility = View.INVISIBLE
                isColorBlack = false
                selectedColor = ""
            }
        }

        blockWhiteColor.setOnClickListener {
            if (!isColorWhite) {
                outlineBlackSelected.visibility = View.INVISIBLE
                outlineWhiteSelected.visibility = View.VISIBLE
                outlineRedSelected.visibility = View.INVISIBLE
                outlineGraySelected.visibility = View.INVISIBLE
                outlineOrangeSelected.visibility = View.INVISIBLE
                outlineBlueSelected.visibility = View.INVISIBLE

                isColorBlack = false
                isColorWhite = true
                isColorRed = false
                isColorGray = false
                isColorOrange = false
                isColorBlue = false

                selectedColor = "White"
            } else {
                outlineWhiteSelected.visibility = View.INVISIBLE
                isColorWhite = false
                selectedColor = ""
            }
        }

        blockRedColor.setOnClickListener {
            if (!isColorRed) {
                outlineBlackSelected.visibility = View.INVISIBLE
                outlineWhiteSelected.visibility = View.INVISIBLE
                outlineRedSelected.visibility = View.VISIBLE
                outlineGraySelected.visibility = View.INVISIBLE
                outlineOrangeSelected.visibility = View.INVISIBLE
                outlineBlueSelected.visibility = View.INVISIBLE

                isColorBlack = false
                isColorWhite = false
                isColorRed = true
                isColorGray = false
                isColorOrange = false
                isColorBlue = false

                selectedColor = "Red"
            } else {
                outlineRedSelected.visibility = View.INVISIBLE
                isColorRed = false
                selectedColor = ""
            }
        }

        blockGrayColor.setOnClickListener {
            if (!isColorGray) {
                outlineBlackSelected.visibility = View.INVISIBLE
                outlineWhiteSelected.visibility = View.INVISIBLE
                outlineRedSelected.visibility = View.INVISIBLE
                outlineGraySelected.visibility = View.VISIBLE
                outlineOrangeSelected.visibility = View.INVISIBLE
                outlineBlueSelected.visibility = View.INVISIBLE

                isColorBlack = false
                isColorWhite = false
                isColorRed = false
                isColorGray = true
                isColorOrange = false
                isColorBlue = false

                selectedColor = "Gray"
            } else {
                outlineGraySelected.visibility = View.INVISIBLE
                isColorGray = false
                selectedColor = ""
            }
        }

        blockOrangeColor.setOnClickListener {
            if (!isColorOrange) {
                outlineBlackSelected.visibility = View.INVISIBLE
                outlineWhiteSelected.visibility = View.INVISIBLE
                outlineRedSelected.visibility = View.INVISIBLE
                outlineGraySelected.visibility = View.INVISIBLE
                outlineOrangeSelected.visibility = View.VISIBLE
                outlineBlueSelected.visibility = View.INVISIBLE

                isColorBlack = false
                isColorWhite = false
                isColorRed = false
                isColorGray = false
                isColorOrange = true
                isColorBlue = false

                selectedColor = "Orange"
            } else {
                outlineOrangeSelected.visibility = View.INVISIBLE
                isColorOrange = false
                selectedColor = ""
            }
        }

        blockBlueColor.setOnClickListener {
            if (!isColorBlue) {
                outlineBlackSelected.visibility = View.INVISIBLE
                outlineWhiteSelected.visibility = View.INVISIBLE
                outlineRedSelected.visibility = View.INVISIBLE
                outlineGraySelected.visibility = View.INVISIBLE
                outlineOrangeSelected.visibility = View.INVISIBLE
                outlineBlueSelected.visibility = View.VISIBLE

                isColorBlack = false
                isColorWhite = false
                isColorRed = false
                isColorGray = false
                isColorOrange = false
                isColorBlue = true

                selectedColor = "Blue"
            } else {
                outlineBlueSelected.visibility = View.INVISIBLE
                isColorBlue = false
                selectedColor = ""
            }
        }

        txtSizeXS.setOnClickListener {
            if (!isSizeXS) {
                txtSizeXS.setBackgroundResource(R.drawable.bg_size_selected)
                txtSizeS.setBackgroundResource(R.drawable.bg_size)
                txtSizeM.setBackgroundResource(R.drawable.bg_size)
                txtSizeL.setBackgroundResource(R.drawable.bg_size)
                txtSizeXL.setBackgroundResource(R.drawable.bg_size)

                txtSizeXS.setTextColor(resources.getColor(R.color.white))
                txtSizeS.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeM.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeL.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeXL.setTextColor(resources.getColor(R.color.black_custom))

                isSizeXS = true
                isSizeS = false
                isSizeM = false
                isSizeL = false
                isSizeXL = false

                selectedSize = "XS"
            } else {
                txtSizeXS.setBackgroundResource(R.drawable.bg_size)
                txtSizeXS.setTextColor(resources.getColor(R.color.black_custom))
                isSizeXS = false
                selectedSize = ""
            }
        }

        txtSizeS.setOnClickListener {
            if (!isSizeS) {
                txtSizeXS.setBackgroundResource(R.drawable.bg_size)
                txtSizeS.setBackgroundResource(R.drawable.bg_size_selected)
                txtSizeM.setBackgroundResource(R.drawable.bg_size)
                txtSizeL.setBackgroundResource(R.drawable.bg_size)
                txtSizeXL.setBackgroundResource(R.drawable.bg_size)

                txtSizeXS.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeS.setTextColor(resources.getColor(R.color.white))
                txtSizeM.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeL.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeXL.setTextColor(resources.getColor(R.color.black_custom))

                isSizeXS = false
                isSizeS = true
                isSizeM = false
                isSizeL = false
                isSizeXL = false

                selectedSize = "S"
            } else {
                txtSizeS.setBackgroundResource(R.drawable.bg_size)
                txtSizeS.setTextColor(resources.getColor(R.color.black_custom))
                isSizeS = false
                selectedSize = ""
            }
        }

        txtSizeM.setOnClickListener {
            if (!isSizeM) {
                txtSizeXS.setBackgroundResource(R.drawable.bg_size)
                txtSizeS.setBackgroundResource(R.drawable.bg_size)
                txtSizeM.setBackgroundResource(R.drawable.bg_size_selected)
                txtSizeL.setBackgroundResource(R.drawable.bg_size)
                txtSizeXL.setBackgroundResource(R.drawable.bg_size)

                txtSizeXS.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeS.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeM.setTextColor(resources.getColor(R.color.white))
                txtSizeL.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeXL.setTextColor(resources.getColor(R.color.black_custom))

                isSizeXS = false
                isSizeS = false
                isSizeM = true
                isSizeL = false
                isSizeXL = false

                selectedSize = "M"
            } else {
                txtSizeM.setBackgroundResource(R.drawable.bg_size)
                txtSizeM.setTextColor(resources.getColor(R.color.black_custom))
                isSizeM = false
                selectedSize = ""
            }
        }

        txtSizeL.setOnClickListener {
            if (!isSizeL) {
                txtSizeXS.setBackgroundResource(R.drawable.bg_size)
                txtSizeS.setBackgroundResource(R.drawable.bg_size)
                txtSizeM.setBackgroundResource(R.drawable.bg_size)
                txtSizeL.setBackgroundResource(R.drawable.bg_size_selected)
                txtSizeXL.setBackgroundResource(R.drawable.bg_size)

                txtSizeXS.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeS.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeM.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeL.setTextColor(resources.getColor(R.color.white))
                txtSizeXL.setTextColor(resources.getColor(R.color.black_custom))

                isSizeXS = false
                isSizeS = false
                isSizeM = false
                isSizeL = true
                isSizeXL = false

                selectedSize = "L"
            } else {
                txtSizeL.setBackgroundResource(R.drawable.bg_size)
                txtSizeL.setTextColor(resources.getColor(R.color.black_custom))
                isSizeL = false
                selectedSize = ""
            }
        }

        txtSizeXL.setOnClickListener {
            if (!isSizeXL) {
                txtSizeXS.setBackgroundResource(R.drawable.bg_size)
                txtSizeS.setBackgroundResource(R.drawable.bg_size)
                txtSizeM.setBackgroundResource(R.drawable.bg_size)
                txtSizeL.setBackgroundResource(R.drawable.bg_size)
                txtSizeXL.setBackgroundResource(R.drawable.bg_size_selected)

                txtSizeXS.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeS.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeM.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeL.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeXL.setTextColor(resources.getColor(R.color.white))

                isSizeXS = false
                isSizeS = false
                isSizeM = false
                isSizeL = false
                isSizeXL = true

                selectedSize = "XL"
            } else {
                txtSizeXL.setBackgroundResource(R.drawable.bg_size)
                txtSizeXL.setTextColor(resources.getColor(R.color.black_custom))
                isSizeXL = false
                selectedSize = ""
            }
        }

        btnAddToCart.setOnClickListener {
            if (isLogin) {
                if(selectedSize == "" || selectedColor == ""){
                    if (selectedSize == "")
                        Toast.makeText(this.requireContext(), "Please choose size", Toast.LENGTH_SHORT).show()
                    if (selectedColor == "")
                        Toast.makeText(this.requireContext(), "Please choose color", Toast.LENGTH_SHORT).show()
                }
                else {
                    val quantitySelectedProduct = dsQuantity.find { it.productId == selectedProduct.productId &&
                            it.productColor == selectedColor &&
                            it.productSize == selectedSize }
                    if(quantitySelectedProduct?.quantity!! > 0){
                        //Add Product to Bag
                        val bagSearch = dsBag.find { it.quantityId == quantitySelectedProduct.quantityId &&
                                it.userAccountName == user.userAccountName}
                        //Neu product da duoc them vao bag
                        if (bagSearch != null) {
                            bagSearch.quantity += 1
                            dbRefBag.child(bagSearch.bagId).setValue(bagSearch)
                        }
                        //Neu product chua co trong bag
                        else {
                            val bag = Bag(
                                "",
                                quantitySelectedProduct.quantityId,
                                user.userAccountName,
                                1
                            )
                            bag.bagId = dbRefBag.push().key.toString()
                            dbRefBag.child(bag.bagId).setValue(bag)
                        }
                        Toast.makeText(this.requireContext(), "This product is added to Cart", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    else
                        Toast.makeText(this.requireContext(), "This product is out of stock", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                val intent = Intent(this.requireContext(), LoginActivity::class.java)
                startForResult.launch(intent)
            }
        }

        dialog.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun eventOnTouchForBottomSheetSort(txtView: TextView?) {
        txtView!!.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    txtView.setBackgroundResource(R.color.red_button)
                    txtView.setTextColor(resources.getColor(R.color.white))
                } else if (event?.action == MotionEvent.ACTION_UP) {
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
        } else {
            txtCategoryActionBar.visibility = View.INVISIBLE
            txtCategory.visibility = View.VISIBLE
            btnStyleShowList.setBackgroundResource(R.drawable.ic_view_module)
            isViewList = true
            hienProductViewList()
        }
    }

    private fun hienProductViewMode() {
        adapterProduct = RvAdapterProduct(dsProduct, R.layout.layout_item, object : EventProductItemListening {
            override fun OnItemClick(pos: Int) {
                val intent =
                    Intent(this@CatalogFragment.requireContext(), ProductDetailActivity::class.java)
                val productSelected = dsProduct[pos]
                val bundlePassing = Bundle()
                bundlePassing.putSerializable("productSelected", productSelected)
                bundlePassing.putBoolean("isLogin", isLogin)
                if (isLogin)
                    bundlePassing.putSerializable("user", user)
                intent.putExtras(bundlePassing)
                startForResult.launch(intent)
            }

            override fun OnItemLongClick(pos: Int) {
                showBottomSheetSelectColorAndSize(dsProduct[pos])
            }

            override fun onClickBtnFavoriteItem(pos: Int) {
                Toast.makeText(this@CatalogFragment.requireContext(), "Do it later", Toast.LENGTH_SHORT).show()
            }
        })
        rcvProduct.adapter = adapterProduct
        rcvProduct.layoutManager = GridLayoutManager(
            this.requireContext(),
            2,
            GridLayoutManager.VERTICAL,
            false
        )
    }

    private fun hienProductViewList() {
        adapterProduct = RvAdapterProduct(dsProduct, R.layout.layout_item2, object : EventProductItemListening {
            override fun OnItemClick(pos: Int) {
                val intent =
                    Intent(this@CatalogFragment.requireContext(), ProductDetailActivity::class.java)
                val productSelected = dsProduct[pos]
                val bundlePassing = Bundle()
                bundlePassing.putSerializable("productSelected", productSelected)
                bundlePassing.putBoolean("isLogin", isLogin)
                if (isLogin)
                    bundlePassing.putSerializable("user", user)
                intent.putExtras(bundlePassing)
                startForResult.launch(intent)
            }

            override fun OnItemLongClick(pos: Int) {
                showBottomSheetSelectColorAndSize(dsProduct[pos])
            }

            override fun onClickBtnFavoriteItem(pos: Int) {
                Toast.makeText(this@CatalogFragment.requireContext(), "Do it later", Toast.LENGTH_SHORT).show()
            }

        })
        rcvProduct.adapter = adapterProduct
        rcvProduct.layoutManager = LinearLayoutManager(
            this.requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    @SuppressLint("SetTextI18n")
    private fun xuLyClickCategory(categorySelected: String) {
        dsProduct.clear()
        if (categorySelected == "All")
            dsProduct.addAll(dsProductAll)
        else if (categorySelected == "Tops")
            dsProduct.addAll(dsProductTops)
        else {
            dsProductByCategory.clear()
            for (product in dsProductAll) {
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
        } else {
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

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val bundleGet = intent?.extras
                if (bundleGet != null) {
                    val requestCode = bundleGet.getInt("requestCode")

                    // Nếu mở màn hình Filter
                    if (requestCode == requestCodeFilter) {
                        val valueFrom = bundleGet.getInt("ValueFrom")
                        val valueTo = bundleGet.getInt("ValueTo")
                        val sizes = bundleGet.getStringArrayList("sizes")
                        val colors = bundleGet.getStringArrayList("colors")

                        dsProduct.clear()
                        dsProduct.addAll(dsProductAll)
                        dsProduct.removeIf { it.productPrice < valueFrom || it.productPrice > valueTo }

                        val listProDuctIdNotHas = mutableListOf<String>()

                        if (sizes!!.isNotEmpty() || colors!!.isNotEmpty()) {
                            listProDuctIdNotHas.clear()
                            for (product in dsProduct) {
                                var isHas = false
                                var quantityFilter =
                                    dsQuantity.filter { quantity -> quantity.productId == product.productId }
                                if (sizes.isNotEmpty())
                                    quantityFilter =
                                        quantityFilter.filter { quantity -> sizes.contains(quantity.productSize) }
                                if (colors!!.isNotEmpty())
                                    quantityFilter =
                                        quantityFilter.filter { quantity -> colors.contains(quantity.productColor) }
                                for (quantity in quantityFilter) {
                                    if (quantity.quantity > 0) {
                                        isHas = true
                                        break
                                    }
                                }
                                if (!isHas) {
                                    listProDuctIdNotHas.add(product.productId)
                                }
                            }
                        }

                        dsProduct.removeIf { listProDuctIdNotHas.contains(it.productId) }

                        if (sortMode == "Ascending")
                            dsProduct.sortBy { it.productPrice }
                        else if (sortMode == "Descending")
                            dsProduct.sortByDescending { it.productPrice }
                        else if (sortMode == "Review")
                            dsProduct.sortByDescending { it.productRating }
                        if (isViewList) {
                            hienProductViewList()
                        } else {
                            hienProductViewMode()
                        }
                    }
                    // Nếu mở màn hình Product Detail hoặc Login
                    else {
                        isLogin = bundleGet.getBoolean("isLogin")
                        this.activity?.intent?.putExtra("isLogin", isLogin)
                        if (isLogin) {
                            user = bundleGet.getSerializable("user") as User
                            this.activity?.intent?.putExtra("user", user)
                        }
                    }
                }
            }
        }
}