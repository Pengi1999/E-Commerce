package com.nhathao.e_commerce.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nhathao.e_commerce.Interfaces.EventShippingAddressItemListening
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.adapters.RvAdapterBagItem
import com.nhathao.e_commerce.adapters.RvAdapterShippingAddress
import com.nhathao.e_commerce.databinding.ActivityCheckoutBinding
import com.nhathao.e_commerce.models.Bag
import com.nhathao.e_commerce.models.Product
import com.nhathao.e_commerce.models.Quantity
import com.nhathao.e_commerce.models.ShippingAddress
import com.nhathao.e_commerce.models.User

private lateinit var binding: ActivityCheckoutBinding
class CheckoutActivity : AppCompatActivity() {
    private lateinit var dbRefBag: DatabaseReference
    private lateinit var dbRefProduct: DatabaseReference
    private lateinit var dbRefQuantity: DatabaseReference
    private lateinit var dbRefShippingAddress: DatabaseReference
    private lateinit var dsBagByUser: MutableList<Bag>
    private lateinit var dsShippingAddressOfUser: MutableList<ShippingAddress>
    private var totalPrice: Int = 0
    private var deliveryPrice: Int = 0
    private var summaryPrice: Int = 0
    private var deliveryMethod: String? = null
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundleGet = intent.extras
        val user = bundleGet?.getSerializable("user") as User

        dbRefBag = FirebaseDatabase.getInstance().getReference("Bags")
        dbRefQuantity = FirebaseDatabase.getInstance().getReference("Quantities")
        dbRefProduct = FirebaseDatabase.getInstance().getReference("Products")
        dbRefShippingAddress = FirebaseDatabase.getInstance().getReference("ShippingAddresses")

        dsBagByUser = mutableListOf()
        dsShippingAddressOfUser = mutableListOf()

        dbRefBag.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalPrice = 0
                dsBagByUser.clear()
                if(snapshot.exists()){
                    for (bagSnap in snapshot.children){
                        if (bagSnap.child("userAccountName").value.toString() == user.userAccountName) {
                            val bagData = Bag(
                                bagSnap.child("bagId").value.toString(),
                                bagSnap.child("quantityId").value.toString(),
                                bagSnap.child("userAccountName").value.toString(),
                                bagSnap.child("quantity").value.toString().toInt()
                            )
                            dsBagByUser.add(bagData)
                        }
                    }
                }
                for (bag in dsBagByUser) {
                    dbRefQuantity.child(bag.quantityId).get().addOnSuccessListener { it ->
                        if (it.exists()){
                            val quantity = Quantity(
                                it.child("quantityId").value.toString(),
                                it.child("productId").value.toString(),
                                it.child("productColor").value.toString(),
                                it.child("productSize").value.toString(),
                                it.child("quantity").value.toString().toInt()
                            )
                            dbRefProduct.child(quantity.productId).get().addOnSuccessListener {
                                if (it.exists()){
                                    val product = Product(
                                        it.child("productId").value.toString(),
                                        it.child("productName").value.toString(),
                                        it.child("productDescribe").value.toString(),
                                        it.child("productImage").value.toString(),
                                        it.child("productSex").value.toString(),
                                        it.child("productType").value.toString(),
                                        it.child("productCategory").value.toString(),
                                        it.child("productBrand").value.toString(),
                                        it.child("productMode").value.toString(),
                                        it.child("productPrice").value.toString().toInt(),
                                        it.child("productRating").value.toString().toFloat(),
                                        it.child("productRatingQuantity").value.toString().toInt()
                                    )
                                    if (product.productMode != "" && product.productMode != "NEW"){
                                        val saleValue = product.productMode.substring(1,3).toFloat() / 100
                                        val salePrice = product.productPrice - (product.productPrice * saleValue).toInt()
                                        totalPrice += salePrice * bag.quantity
                                    }
                                    else
                                        totalPrice += product.productPrice * bag.quantity

                                    binding.txtOrderPrice.text = "$totalPrice$"
                                    summaryPrice = totalPrice + deliveryPrice
                                    binding.txtSummaryPrice.text = "$summaryPrice$"
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        dbRefShippingAddress.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                dsShippingAddressOfUser.clear()
                if (snapshot.exists()) {
                    for (shippingAddressSnap in snapshot.children) {
                        if (shippingAddressSnap.child("userAccountName").value.toString() == user.userAccountName &&
                            shippingAddressSnap.child("status").value.toString().toBoolean()){
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
                    for (shippingAddressChildren in dsShippingAddressOfUser) {
                        if (shippingAddressChildren.used) {
                            binding.txtFullName.text = shippingAddressChildren.fullName
                            binding.txtAddress.text = shippingAddressChildren.address
                            binding.txtAddress2.text = "${shippingAddressChildren.city}, ${shippingAddressChildren.region} ${shippingAddressChildren.zipCode}, ${shippingAddressChildren.country}"
                            break
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

        binding.txtChangeShippingAddress.setOnClickListener {
            val intent = Intent(this, ShippingAddressActivity::class.java)
            val bundlePassing = Bundle()
            bundlePassing.putSerializable("user", user)
            intent.putExtras(bundlePassing)
            startActivity(intent)
        }

        binding.txtChangePayment.setOnClickListener {
            Toast.makeText(this, "Do it later", Toast.LENGTH_SHORT).show()
        }

        binding.cardDeliveryMethodFedEx.setOnClickListener {
            if (deliveryMethod != "FedEx") {
                binding.imgSelectedFedEx.visibility = View.VISIBLE
                binding.imgSelectedUSPS.visibility = View.INVISIBLE
                binding.imgSelectedDHL.visibility = View.INVISIBLE
                deliveryMethod = "FedEx"
                deliveryPrice = 15
                binding.txtDeliveryPrice.text = "$deliveryPrice$"
                summaryPrice = totalPrice + deliveryPrice
                binding.txtSummaryPrice.text = "$summaryPrice$"
            }
        }

        binding.cardDeliveryMethodUSPS.setOnClickListener {
            if (deliveryMethod != "USPS") {
                binding.imgSelectedFedEx.visibility = View.INVISIBLE
                binding.imgSelectedUSPS.visibility = View.VISIBLE
                binding.imgSelectedDHL.visibility = View.INVISIBLE
                deliveryMethod = "USPS"
                deliveryPrice = 16
                binding.txtDeliveryPrice.text = "$deliveryPrice$"
                summaryPrice = totalPrice + deliveryPrice
                binding.txtSummaryPrice.text = "$summaryPrice$"
            }
        }

        binding.cardDeliveryMethodDHL.setOnClickListener {
            if (deliveryMethod != "DHL") {
                binding.imgSelectedFedEx.visibility = View.INVISIBLE
                binding.imgSelectedUSPS.visibility = View.INVISIBLE
                binding.imgSelectedDHL.visibility = View.VISIBLE
                deliveryMethod = "DHL"
                deliveryPrice = 17
                binding.txtDeliveryPrice.text = "$deliveryPrice$"
                summaryPrice = totalPrice + deliveryPrice
                binding.txtSummaryPrice.text = "$summaryPrice$"
            }
        }

        binding.btnSubmitOrder.setOnClickListener {
            Toast.makeText(this, "Do it later", Toast.LENGTH_SHORT).show()
        }
    }
}