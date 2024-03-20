package com.nhathao.e_commerce.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nhathao.e_commerce.Interfaces.EventMyOrderItemListening
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.activities.VisualSearchActivity
import com.nhathao.e_commerce.adapters.RvAdapterOrderItem
import com.nhathao.e_commerce.models.Order
import com.nhathao.e_commerce.models.User

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyOrdersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyOrdersFragment : Fragment() {
    private lateinit var dbRefOrder: DatabaseReference
    private lateinit var btnBack: ImageButton
    private lateinit var btnSearch: ImageButton
    private lateinit var txtDeliveredStatus: TextView
    private lateinit var txtProcessingStatus: TextView
    private lateinit var txtCancelledStatus: TextView
    private lateinit var rcvOrder: RecyclerView
    private lateinit var dsDeliveredOrderOfUser: MutableList<Order>
    private lateinit var dsProcessingOrderOfUser: MutableList<Order>
    private lateinit var dsCancelledOrderOfUser: MutableList<Order>
    private lateinit var user: User
    private var isLogin: Boolean = false
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
        val view = inflater.inflate(R.layout.fragment_my_orders, container, false)

        btnBack = view.findViewById(R.id.btnBack)
        btnSearch = view.findViewById(R.id.btnSearch)
        txtDeliveredStatus = view.findViewById(R.id.txtDeliveredStatus)
        txtProcessingStatus = view.findViewById(R.id.txtProcessingStatus)
        txtCancelledStatus = view.findViewById(R.id.txtCancelledStatus)
        rcvOrder = view.findViewById(R.id.rcvOrder)

        val bundleGetData = this.activity?.intent?.extras
        if (bundleGetData != null) {
            isLogin = bundleGetData.getBoolean("isLogin")
            if (isLogin)
                user = bundleGetData.getSerializable("user") as User
        }

        dbRefOrder = FirebaseDatabase.getInstance().getReference("Orders")

        dsDeliveredOrderOfUser = mutableListOf()
        dsProcessingOrderOfUser = mutableListOf()
        dsCancelledOrderOfUser = mutableListOf()

        dbRefOrder.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dsDeliveredOrderOfUser.clear()
                dsProcessingOrderOfUser.clear()
                dsCancelledOrderOfUser.clear()
                if (snapshot.exists()) {
                    for (orderSnap in snapshot.children) {
                        if (orderSnap.child("userAccountName").value.toString() == user.userAccountName) {
                            val order = Order(
                                orderSnap.child("orderId").value.toString(),
                                orderSnap.child("userAccountName").value.toString(),
                                orderSnap.child("shippingAddressId").value.toString(),
                                orderSnap.child("paymentMethod").value.toString(),
                                orderSnap.child("deliveryMethod").value.toString(),
                                orderSnap.child("promoCode").value.toString(),
                                orderSnap.child("totalPrice").value.toString().toInt(),
                                orderSnap.child("createTime").value.toString().toLong(),
                                orderSnap.child("status").value.toString()
                            )
                            if (order.status == "Delivered")
                                dsDeliveredOrderOfUser.add(order)
                            else if (order.status == "Cancelled")
                                dsCancelledOrderOfUser.add(order)
                            else
                                dsProcessingOrderOfUser.add(order)
                        }
                    }
                    val orderAdapter = RvAdapterOrderItem(dsDeliveredOrderOfUser, object : EventMyOrderItemListening{
                        override fun onClickDetailsItemListening(pos: Int) {
                            this@MyOrdersFragment.activity?.intent?.putExtra("selectedOrder", dsDeliveredOrderOfUser[pos])
                            this@MyOrdersFragment.activity?.supportFragmentManager?.beginTransaction()?.apply {
                                addToBackStack(null)
                                replace(R.id.frame_layout, OrderDetailsFragment())
                                commit()
                            }
                        }
                    })
                    rcvOrder.adapter = orderAdapter
                    rcvOrder.layoutManager = LinearLayoutManager(
                        this@MyOrdersFragment.requireContext(),
                        LinearLayoutManager.VERTICAL,
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

        btnSearch.setOnClickListener {
            val intent = Intent(this.requireContext(), VisualSearchActivity::class.java)
            startActivity(intent)
        }

        txtDeliveredStatus.setOnClickListener {
            val orderAdapter = RvAdapterOrderItem(dsDeliveredOrderOfUser, object : EventMyOrderItemListening{
                override fun onClickDetailsItemListening(pos: Int) {
                    this@MyOrdersFragment.activity?.intent?.putExtra("selectedOrder", dsDeliveredOrderOfUser[pos])
                    this@MyOrdersFragment.activity?.supportFragmentManager?.beginTransaction()?.apply {
                        addToBackStack(null)
                        replace(R.id.frame_layout, OrderDetailsFragment())
                        commit()
                    }
                }
            })
            rcvOrder.adapter = orderAdapter

            txtDeliveredStatus.setBackgroundResource(R.drawable.bg_order_status)
            txtDeliveredStatus.setTextColor(resources.getColor(R.color.white))

            txtProcessingStatus.background = null
            txtProcessingStatus.setTextColor(resources.getColor(R.color.black_custom))

            txtCancelledStatus.background = null
            txtCancelledStatus.setTextColor(resources.getColor(R.color.black_custom))
        }

        txtProcessingStatus.setOnClickListener {
            val orderAdapter = RvAdapterOrderItem(dsProcessingOrderOfUser, object : EventMyOrderItemListening{
                override fun onClickDetailsItemListening(pos: Int) {
                    this@MyOrdersFragment.activity?.intent?.putExtra("selectedOrder", dsProcessingOrderOfUser[pos])
                    this@MyOrdersFragment.activity?.supportFragmentManager?.beginTransaction()?.apply {
                        addToBackStack(null)
                        replace(R.id.frame_layout, OrderDetailsFragment())
                        commit()
                    }
                }
            })
            rcvOrder.adapter = orderAdapter

            txtProcessingStatus.setBackgroundResource(R.drawable.bg_order_status)
            txtProcessingStatus.setTextColor(resources.getColor(R.color.white))

            txtDeliveredStatus.background = null
            txtDeliveredStatus.setTextColor(resources.getColor(R.color.black_custom))

            txtCancelledStatus.background = null
            txtCancelledStatus.setTextColor(resources.getColor(R.color.black_custom))
        }

        txtCancelledStatus.setOnClickListener {
            val orderAdapter = RvAdapterOrderItem(dsCancelledOrderOfUser, object : EventMyOrderItemListening{
                override fun onClickDetailsItemListening(pos: Int) {
                    this@MyOrdersFragment.activity?.intent?.putExtra("selectedOrder", dsCancelledOrderOfUser[pos])
                    this@MyOrdersFragment.activity?.supportFragmentManager?.beginTransaction()?.apply {
                        addToBackStack(null)
                        replace(R.id.frame_layout, OrderDetailsFragment())
                        commit()
                    }
                }
            })
            rcvOrder.adapter = orderAdapter

            txtCancelledStatus.setBackgroundResource(R.drawable.bg_order_status)
            txtCancelledStatus.setTextColor(resources.getColor(R.color.white))

            txtDeliveredStatus.background = null
            txtDeliveredStatus.setTextColor(resources.getColor(R.color.black_custom))

            txtProcessingStatus.background = null
            txtProcessingStatus.setTextColor(resources.getColor(R.color.black_custom))
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
         * @return A new instance of fragment MyOrdersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyOrdersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}