package com.nhathao.e_commerce.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.models.Order
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
    private lateinit var btnBack: ImageButton
    private lateinit var btnSearch: ImageButton
    private lateinit var btnReorder: Button
    private lateinit var btnLeaveFeedback: Button
    private lateinit var txtOrderId: TextView
    private lateinit var txtCreateTime: TextView
    private lateinit var txtItemQuantity: TextView
    private lateinit var txtOrderStatus: TextView
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

        btnBack = view.findViewById(R.id.btnBack)
        btnSearch = view.findViewById(R.id.btnSearch)
        btnReorder = view.findViewById(R.id.btnReorder)
        btnLeaveFeedback = view.findViewById(R.id.btnLeaveFeedback)
        txtOrderId = view.findViewById(R.id.txtOrderId)
        txtCreateTime = view.findViewById(R.id.txtCreateTime)
        txtItemQuantity = view.findViewById(R.id.txtItemQuantity)
        txtOrderStatus = view.findViewById(R.id.txtOrderStatus)

        selectedOrder = this.activity?.intent?.getSerializableExtra("selectedOrder") as Order

        txtOrderId.text = "Order ${selectedOrder.orderId}"
        txtCreateTime.text = dateFormat.format(selectedOrder.createTime)


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