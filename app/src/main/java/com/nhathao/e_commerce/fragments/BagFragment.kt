package com.nhathao.e_commerce.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nhathao.e_commerce.Interfaces.EventShippingAddressItemListening
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.activities.CheckoutActivity
import com.nhathao.e_commerce.activities.EditingShippingAddressActivity
import com.nhathao.e_commerce.activities.ShippingAddressActivity
import com.nhathao.e_commerce.adapters.RvAdapterBagItem
import com.nhathao.e_commerce.adapters.RvAdapterReview
import com.nhathao.e_commerce.adapters.RvAdapterShippingAddress
import com.nhathao.e_commerce.models.Bag
import com.nhathao.e_commerce.models.Product
import com.nhathao.e_commerce.models.Quantity
import com.nhathao.e_commerce.models.ShippingAddress
import com.nhathao.e_commerce.models.User

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BagFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BagFragment : Fragment() {
    private lateinit var dialog: BottomSheetDialog
    private lateinit var dbRefBag: DatabaseReference
    private lateinit var dbRefProduct: DatabaseReference
    private lateinit var dbRefQuantity: DatabaseReference
    private lateinit var dbRefShippingAddress: DatabaseReference
    private lateinit var btnSearch: ImageButton
    private lateinit var btnChoosePromoCode: ImageButton
    private lateinit var btnClearPromoCode: ImageButton
    private lateinit var btnCheckOut: Button
    private lateinit var edtPromoCode: EditText
    private lateinit var txtTotalPrice: TextView
    private lateinit var rcvBag: RecyclerView
    private lateinit var dsBagByUser: MutableList<Bag>
    private lateinit var dsQuantityByBag: MutableList<Quantity>
    private lateinit var dsProductByBag: MutableList<Product>
    private lateinit var user: User
    private var isLogin: Boolean = false
    private var totalPrice: Int = 0
    private var shippingAddressOfUser: ShippingAddress? = null
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
        val view = inflater.inflate(R.layout.fragment_bag, container, false)

        btnSearch = view.findViewById(R.id.btnSearch)
        btnChoosePromoCode = view.findViewById(R.id.btnChoosePromoCode)
        btnClearPromoCode = view.findViewById(R.id.btnClearPromoCode)
        btnCheckOut = view.findViewById(R.id.btnCheckOut)
        edtPromoCode = view.findViewById(R.id.edtPromoCode)
        txtTotalPrice = view.findViewById(R.id.txtTotalPrice)
        rcvBag = view.findViewById(R.id.rcvBag)

        val bundleGetData = this.activity?.intent?.extras
        if (bundleGetData != null) {
            isLogin = bundleGetData.getBoolean("isLogin")
            if (isLogin)
                user = bundleGetData.getSerializable("user") as User
        }

        dbRefBag = FirebaseDatabase.getInstance().getReference("Bags")
        dbRefQuantity = FirebaseDatabase.getInstance().getReference("Quantities")
        dbRefProduct = FirebaseDatabase.getInstance().getReference("Products")
        dbRefShippingAddress = FirebaseDatabase.getInstance().getReference("ShippingAddresses")

        dsBagByUser = mutableListOf()
        dsQuantityByBag = mutableListOf()
        dsProductByBag = mutableListOf()

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
                    val bagAdapter = RvAdapterBagItem(dsBagByUser)
                    rcvBag.adapter = bagAdapter
                    rcvBag.layoutManager = LinearLayoutManager(
                        this@BagFragment.requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
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

                                    txtTotalPrice.text = "$totalPrice$"
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
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (shippingAddressSnap in snapshot.children) {
                        if (shippingAddressSnap.child("userAccountName").value.toString() == user.userAccountName &&
                            shippingAddressSnap.child("status").value.toString().toBoolean() &&
                            shippingAddressSnap.child("used").value.toString().toBoolean()){
                            shippingAddressOfUser = ShippingAddress(
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
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        btnSearch.setOnClickListener {
            Toast.makeText(this.requireContext(), "Do it later", Toast.LENGTH_SHORT).show()
        }

        btnChoosePromoCode.setOnClickListener {
            showBottomSheetPromoCode()
        }

        edtPromoCode.setOnClickListener {
            showBottomSheetPromoCode()
        }

        btnClearPromoCode.setOnClickListener {
            edtPromoCode.setText("")
            btnChoosePromoCode.visibility = View.VISIBLE
            btnClearPromoCode.visibility = View.INVISIBLE
            txtTotalPrice.text = "$totalPrice$"
        }

        btnCheckOut.setOnClickListener {
            if (dsBagByUser.size != 0) {
                if (shippingAddressOfUser != null) {
                    val intent = Intent(this.requireContext(), CheckoutActivity::class.java)
                    val bundlePassing = Bundle()
                    bundlePassing.putSerializable("user", user)
                    intent.putExtras(bundlePassing)
                    startActivity(intent)
                }
                else {
                    val intent = Intent(this.requireContext(), ShippingAddressActivity::class.java)
                    val bundlePassing = Bundle()
                    bundlePassing.putSerializable("user", user)
                    intent.putExtras(bundlePassing)
                    startActivity(intent)
                }
            }
            else
                Toast.makeText(this.requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun showBottomSheetPromoCode() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_promocode, null)
        dialog = BottomSheetDialog(this.requireContext())
        dialog.setContentView(dialogView)

        val btnApplyPersonal10 = dialogView.findViewById<Button>(R.id.btnApplyPersonal10)
        val btnApplySummerSale = dialogView.findViewById<Button>(R.id.btnApplySummerSale)
        val btnApplyPersonal22 = dialogView.findViewById<Button>(R.id.btnApplyPersonal22)

        btnApplyPersonal10.setOnClickListener {
            edtPromoCode.setText("personalpromocode10")
            btnClearPromoCode.visibility = View.VISIBLE
            btnChoosePromoCode.visibility = View.INVISIBLE
            val totalPriceWithPromoCode = totalPrice - (totalPrice * 0.1).toInt()
            txtTotalPrice.text = "$totalPriceWithPromoCode$"
            dialog.dismiss()
        }

        btnApplySummerSale.setOnClickListener {
            edtPromoCode.setText("summer2024")
            btnClearPromoCode.visibility = View.VISIBLE
            btnChoosePromoCode.visibility = View.INVISIBLE
            val totalPriceWithPromoCode = totalPrice - (totalPrice * 0.15).toInt()
            txtTotalPrice.text = "$totalPriceWithPromoCode$"
            dialog.dismiss()
        }

        btnApplyPersonal22.setOnClickListener {
            edtPromoCode.setText("personalpromocode22")
            btnClearPromoCode.visibility = View.VISIBLE
            btnChoosePromoCode.visibility = View.INVISIBLE
            val totalPriceWithPromoCode = totalPrice - (totalPrice * 0.22).toInt()
            txtTotalPrice.text = "$totalPriceWithPromoCode$"
            dialog.dismiss()
        }

        dialog.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BagFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BagFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}