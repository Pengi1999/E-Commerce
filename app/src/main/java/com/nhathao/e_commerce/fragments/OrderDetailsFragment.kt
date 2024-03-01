package com.nhathao.e_commerce.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.adapters.RvAdapterBagItem
import com.nhathao.e_commerce.adapters.RvAdapterOrderDetailsItem
import com.nhathao.e_commerce.models.Bag
import com.nhathao.e_commerce.models.Order
import com.nhathao.e_commerce.models.OrderDetail
import com.nhathao.e_commerce.models.ShippingAddress
import com.nhathao.e_commerce.models.User
import java.text.SimpleDateFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@SuppressLint("SimpleDateFormat")
class OrderDetailsFragment : Fragment() {
    private lateinit var dbRefOrder: DatabaseReference
    private lateinit var dbRefOrderDetail: DatabaseReference
    private lateinit var dbRefShippingAddress: DatabaseReference
    private lateinit var btnBack: ImageButton
    private lateinit var btnSearch: ImageButton
    private lateinit var btnReorder: Button
    private lateinit var btnLeaveFeedback: Button
    private lateinit var txtOrderId: TextView
    private lateinit var txtCreateTime: TextView
    private lateinit var txtItemQuantity: TextView
    private lateinit var txtOrderStatus: TextView
    private lateinit var txtShippingAddress: TextView
    private lateinit var txtPaymentMethod: TextView
    private lateinit var txtDeliveryMethod: TextView
    private lateinit var txtDiscount: TextView
    private lateinit var txtTotalAmount: TextView
    private lateinit var rcvOrderDetail: RecyclerView
    private lateinit var dsOrderDetails: MutableList<OrderDetail>
    private lateinit var selectedOrder: Order
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy")
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
        val view = inflater.inflate(R.layout.fragment_order_details, container, false)

        dbRefShippingAddress = FirebaseDatabase.getInstance().getReference("ShippingAddresses")
        dbRefOrderDetail = FirebaseDatabase.getInstance().getReference("OrderDetails")

        btnBack = view.findViewById(R.id.btnBack)
        btnSearch = view.findViewById(R.id.btnSearch)
        btnReorder = view.findViewById(R.id.btnReorder)
        btnLeaveFeedback = view.findViewById(R.id.btnLeaveFeedback)
        txtOrderId = view.findViewById(R.id.txtOrderId)
        txtCreateTime = view.findViewById(R.id.txtCreateTime)
        txtItemQuantity = view.findViewById(R.id.txtItemQuantity)
        txtOrderStatus = view.findViewById(R.id.txtOrderStatus)
        txtShippingAddress = view.findViewById(R.id.txtShippingAddress)
        txtPaymentMethod = view.findViewById(R.id.txtPaymentMethod)
        txtDeliveryMethod = view.findViewById(R.id.txtDeliveryMethod)
        txtDiscount = view.findViewById(R.id.txtDiscount)
        txtTotalAmount = view.findViewById(R.id.txtTotalAmount)
        rcvOrderDetail = view.findViewById(R.id.rcvOrderDetail)

        selectedOrder = this.activity?.intent?.getSerializableExtra("selectedOrder") as Order

        txtOrderId.text = "Order ${selectedOrder.orderId}"
        txtCreateTime.text = dateFormat.format(selectedOrder.createTime)
        txtOrderStatus.text = selectedOrder.status

        if (selectedOrder.status == "Delivered")
            txtOrderStatus.setTextColor(Color.parseColor("#2AA952"))
        else if (selectedOrder.status == "Cancelled")
            txtOrderStatus.setTextColor(resources.getColor(R.color.text_color_hint))
        else
            txtOrderStatus.setTextColor(resources.getColor(R.color.red_button))

        dsOrderDetails = mutableListOf()

        dbRefOrderDetail.get().addOnSuccessListener {
            if (it.exists()){
                var quantityValue = 0
                dsOrderDetails.clear()
                for (orderDetailSnap in it.children) {
                    if (orderDetailSnap.child("orderId").value.toString() == selectedOrder.orderId) {
                        quantityValue += orderDetailSnap.child("quantity").value.toString().toInt()
                        val orderDetail = OrderDetail(
                            orderDetailSnap.child("orderDetailId").value.toString(),
                            orderDetailSnap.child("orderId").value.toString(),
                            orderDetailSnap.child("quantityId").value.toString(),
                            orderDetailSnap.child("quantity").value.toString().toInt()
                        )
                        dsOrderDetails.add(orderDetail)
                    }
                }
                txtItemQuantity.text = "$quantityValue items"
                val orderDetailAdapter = RvAdapterOrderDetailsItem(dsOrderDetails)
                rcvOrderDetail.adapter = orderDetailAdapter
                rcvOrderDetail.layoutManager = LinearLayoutManager(
                    this.requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
            }
        }

        dbRefShippingAddress.child(selectedOrder.shippingAddressId).get().addOnSuccessListener {
            if (it.exists()){
                val shippingAddress = ShippingAddress(
                    it.child("shippingAddressId").value.toString(),
                    it.child("userAccountName").value.toString(),
                    it.child("fullName").value.toString(),
                    it.child("address").value.toString(),
                    it.child("city").value.toString(),
                    it.child("region").value.toString(),
                    it.child("zipCode").value.toString(),
                    it.child("country").value.toString(),
                    it.child("used").value.toString().toBoolean(),
                    it.child("status").value.toString().toBoolean(),
                )
                txtShippingAddress.text = "${shippingAddress.address}, ${shippingAddress.city}, ${shippingAddress.region} ${shippingAddress.zipCode}, ${shippingAddress.country}"
            }
        }

        txtPaymentMethod.text = selectedOrder.paymentMethod

        when (selectedOrder.deliveryMethod) {
            "FedEx" -> txtDeliveryMethod.text = "${selectedOrder.deliveryMethod}, 3 days, 15$"
            "USPS" -> txtDeliveryMethod.text = "${selectedOrder.deliveryMethod}, 3 days, 16$"
            "DHL" -> txtDeliveryMethod.text = "${selectedOrder.deliveryMethod}, 2 days, 17$"
        }

        when (selectedOrder.promoCode) {
            "personalpromocode10" -> txtDiscount.text = "10%, Personal promo code"
            "summer2024" -> txtDiscount.text = "15%, Summer Sale promo code"
            "personalpromocode22" -> txtDiscount.text = "22%, Personal promo code"
        }

        txtTotalAmount.text = "${selectedOrder.totalPrice}$"

        btnBack.setOnClickListener {
            this.activity?.supportFragmentManager?.popBackStack()
        }

        btnSearch.setOnClickListener {
            Toast.makeText(this.requireContext(), "Do it later", Toast.LENGTH_SHORT).show()
        }

        btnReorder.setOnClickListener {
            Toast.makeText(this.requireContext(), "Do it later", Toast.LENGTH_SHORT).show()
        }

        btnLeaveFeedback.setOnClickListener {
            Toast.makeText(this.requireContext(), "Do it later", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrderDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}