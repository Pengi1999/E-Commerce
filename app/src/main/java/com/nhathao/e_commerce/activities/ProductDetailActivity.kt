package com.nhathao.e_commerce.activities

import android.app.Activity
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
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.nhathao.e_commerce.models.Quantity
import com.nhathao.e_commerce.models.User

private lateinit var binding: ActivityProductDetailBinding
class ProductDetailActivity : AppCompatActivity() {
    private lateinit var dbRefProduct: DatabaseReference
    private lateinit var dbRefQuantity: DatabaseReference
    private lateinit var dsProduct: MutableList<Product>
    private lateinit var dsQuantity: MutableList<Quantity>
    private lateinit var productSelected: Product
    private lateinit var dialog: BottomSheetDialog
    private lateinit var user: User
    private var selectedSize = ""
    private var selectedColor = ""
    private var isLogin: Boolean = false
    private var requestCodeSelectItem = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundleGet = intent.extras
        productSelected = bundleGet?.getSerializable("productSelected") as Product
        isLogin = bundleGet.getBoolean("isLogin")
        if (isLogin)
            user = bundleGet.getSerializable("user") as User

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
        binding.ratingProduct.rating = productSelected.productRating!!.toFloat()
        binding.txtRatingQuantity.text = "(${productSelected.productRatingQuantity})"

        dbRefProduct = FirebaseDatabase.getInstance().getReference("Products")
        dbRefQuantity = FirebaseDatabase.getInstance().getReference("Quantities")

        dsProduct = mutableListOf()
        dsQuantity = mutableListOf()

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
                        if(productData.productCategory == productSelected.productCategory && productData.productId != productSelected.productId)
                            dsProduct.add(productData)
                    }
                    binding.txtNumberOfItem.text = "${dsProduct.size} items"
                    val mAdapter =
                        RvAdapterProduct(dsProduct, R.layout.layout_item, object : RvInterface {
                            override fun OnItemClick(pos: Int) {
                                val intent = Intent(this@ProductDetailActivity, ProductDetailActivity::class.java)
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
        dbRefQuantity.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                dsQuantity.clear()
                if(snapshot.exists()){
                    for (quantitySnap in snapshot.children){
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

        binding.btnBack.setOnClickListener {
            val data = Intent()
            val bundlePassing = Bundle()
            bundlePassing.putInt("requestCode", requestCodeSelectItem)
            bundlePassing.putBoolean("isLogin", isLogin)
            if (isLogin)
                bundlePassing.putSerializable("user", user)
            data.putExtras(bundlePassing)
            setResult(Activity.RESULT_OK, data)
            finish()
        }

        binding.btnAddToCart.setOnClickListener {
            if (isLogin) {
                if(selectedSize == "" || selectedColor == ""){
                    if (selectedSize == "")
                        binding.blockSizeSelect.setBackgroundResource(R.drawable.bg_sizeandcolor_productdetail_error)
                    if (selectedColor == "")
                        binding.blockColorSelect.setBackgroundResource(R.drawable.bg_sizeandcolor_productdetail_error)
                }
                else {
                    val quantitySelectedProduct = dsQuantity.find { it.productId == productSelected.productId &&
                            it.productColor == selectedColor &&
                            it.productSize == selectedSize }
                    if(quantitySelectedProduct?.quantity!! > 0)
                    //Add to Cart
                        dialog.dismiss()
                    else
                        Toast.makeText(this, "This product is out of stock", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                val intent = Intent(this, LoginActivity::class.java)
                startForResult.launch(intent)
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
            val bundlePassing = Bundle()
            bundlePassing.putSerializable("productSelected", productSelected)
            bundlePassing.putBoolean("isLogin", isLogin)
            if (isLogin)
                bundlePassing.putSerializable("user", user)
            intent.putExtras(bundlePassing)
            startForResult.launch(intent)
        }

        binding.blockItemDetails.setOnClickListener {
            Toast.makeText(this, "Do it later", Toast.LENGTH_SHORT).show()
        }

        binding.blockShippingInfo.setOnClickListener {
            Toast.makeText(this, "Do it later", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showBottomSheetSelectColorAndSize(selectedProduct: Product) {
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
            if (isLogin) {
                if(selectedSize == "" || selectedColor == ""){
                    if (selectedSize == "")
                        Toast.makeText(this, "Please choose size", Toast.LENGTH_SHORT).show()
                    if (selectedColor == "")
                        Toast.makeText(this, "Please choose color", Toast.LENGTH_SHORT).show()
                }
                else {
                    val quantitySelectedProduct = dsQuantity.find { it.productId == selectedProduct.productId &&
                            it.productColor == selectedColor &&
                            it.productSize == selectedSize }
                    if(quantitySelectedProduct?.quantity!! > 0)
                    //Add to Cart
                        dialog.dismiss()
                    else
                        Toast.makeText(this, "This product is out of stock", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                val intent = Intent(this, LoginActivity::class.java)
                startForResult.launch(intent)
            }
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

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val bundleGet = intent?.extras
                if (bundleGet != null) {
                    isLogin = bundleGet.getBoolean("isLogin")
                    if(isLogin)
                        user = bundleGet.getSerializable("user") as User
                }
            }
        }
}