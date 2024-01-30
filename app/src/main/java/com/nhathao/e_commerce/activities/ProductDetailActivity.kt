package com.nhathao.e_commerce.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nhathao.e_commerce.Interfaces.RvInterface
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.adapters.RvAdapterProduct
import com.nhathao.e_commerce.databinding.ActivityProductDetailBinding
import com.nhathao.e_commerce.models.Product
import com.nhathao.e_commerce.models.User

private lateinit var binding: ActivityProductDetailBinding
class ProductDetailActivity : AppCompatActivity() {
    private lateinit var dbRefProduct: DatabaseReference
    private lateinit var dsProduct: MutableList<Product>
    private lateinit var productSelected: Product
    private lateinit var dialog: BottomSheetDialog
    private var selectedSize = ""
    private var selectedColor = ""
    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras

        if (bundle!!.getSerializable("user") != null)
            user = bundle.getSerializable("user") as User
        productSelected = bundle.getSerializable("productSelected") as Product

        val bytes = Base64.decode(productSelected.productImage, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        binding.imgProduct.setImageBitmap(bitmap)
        binding.txtProductCategory.text = productSelected.productCategory
        binding.txtProductName.text = productSelected.productName
        binding.txtProductDes.text = productSelected.productDescribe

        if (productSelected.productMode.contains("-")){
            val saleValue = productSelected.productMode.substring(1,3).toFloat() / 100
            binding.txtProductPrice.setTextColor(resources.getColor(R.color.text_color_hint))
            binding.txtProductPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.txtProductPriceSale.text = "$${productSelected.productPrice - (productSelected.productPrice * saleValue).toInt()}"
        }
        binding.txtProductPrice.text = "$${productSelected.productPrice}"
//        binding.ratingProduct.rating = productSelected.productRating!!.toFloat()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAddToCart.setOnClickListener {
            if(selectedSize == "" || selectedColor == ""){
                if (selectedSize == "")
                    binding.blockSizeSelect.setBackgroundResource(R.drawable.bg_sizeandcolor_productdetail_error)
                if (selectedColor == "")
                    binding.blockColorSelect.setBackgroundResource(R.drawable.bg_sizeandcolor_productdetail_error)
            }
            else {
                finish()
            }
        }

        binding.blockSizeSelect.setOnClickListener {
            showBottomSheetSizeSelect()
        }

        binding.blockColorSelect.setOnClickListener {
            showBottomSheetColorSelect()
        }

        binding.blockReview.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("productSelected", productSelected)
            if (user != null)
                bundle.putSerializable("user", user)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        dbRefProduct = FirebaseDatabase.getInstance().getReference("Products")
        dsProduct = mutableListOf()

        dbRefProduct.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                dsProduct.clear()
                if(snapshot.exists()){
                    for (productSnap in snapshot.children){
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
                        if(productData.productCategory == productSelected.productCategory)
                            dsProduct.add(productData)
                    }
                    binding.txtNumberOfItem.text = "${dsProduct.size} items"
                    val mAdapter =
                        RvAdapterProduct(dsProduct, R.layout.layout_item, object : RvInterface {
                            override fun OnItemClick(pos: Int) {
                                val intent = Intent(this@ProductDetailActivity, ProductDetailActivity::class.java)
                                val productSelected = dsProduct[pos]
                                val bundle = Bundle()
                                bundle.putSerializable("productSelected", productSelected)
                                intent.putExtras(bundle)
                                startActivity(intent)
                            }

                            override fun OnItemLongClick(pos: Int) {
                                showBottomSheetSelectColorAndSize()
                            }
                        })
                    binding.rvSuggest.adapter = mAdapter
                    binding.rvSuggest.layoutManager = LinearLayoutManager(
                        this@ProductDetailActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun showBottomSheetSelectColorAndSize() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_select_colorandsize, null)
        dialog = BottomSheetDialog(this)
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
        val outlineOrangeSelected = dialogView.findViewById<LinearLayout>(R.id.outlineOrangeSelected)
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
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showBottomSheetSizeSelect() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_select_size, null)
        dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        val txtSizeXS = dialogView.findViewById<TextView>(R.id.txtSizeXS)
        val txtSizeS = dialogView.findViewById<TextView>(R.id.txtSizeS)
        val txtSizeM = dialogView.findViewById<TextView>(R.id.txtSizeM)
        val txtSizeL = dialogView.findViewById<TextView>(R.id.txtSizeL)
        val txtSizeXL = dialogView.findViewById<TextView>(R.id.txtSizeXL)

        val btnApply = dialogView.findViewById<Button>(R.id.btnApply)

        var isSizeXS: Boolean = false
        var isSizeS: Boolean = false
        var isSizeM: Boolean = false
        var isSizeL: Boolean = false
        var isSizeXL: Boolean = false

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

        btnApply.setOnClickListener {
            if (selectedSize != "") {
                binding.txtSize.text = selectedSize
                binding.blockSizeSelect.setBackgroundResource(R.drawable.bg_sizeandcolor_productdetail)
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showBottomSheetColorSelect() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_select_color, null)
        dialog = BottomSheetDialog(this)
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
        val outlineOrangeSelected = dialogView.findViewById<LinearLayout>(R.id.outlineOrangeSelected)
        val outlineBlueSelected = dialogView.findViewById<LinearLayout>(R.id.outlineBlueSelected)

        val btnApply = dialogView.findViewById<Button>(R.id.btnApply)

        var isColorBlack: Boolean = false
        var isColorWhite: Boolean = false
        var isColorRed: Boolean = false
        var isColorGray: Boolean = false
        var isColorOrange: Boolean = false
        var isColorBlue: Boolean = false

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

        btnApply.setOnClickListener {
            if (selectedColor != ""){
                binding.txtColor.text = selectedColor
                binding.blockColorSelect.setBackgroundResource(R.drawable.bg_sizeandcolor_productdetail)
            }
            dialog.dismiss()
        }
        dialog.show()
    }
}