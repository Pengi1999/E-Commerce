package com.nhathao.e_commerce.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nhathao.e_commerce.Interfaces.EventMyOrderItemListening
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.models.Order
import java.text.SimpleDateFormat

class RvAdapterOrderItem (private var ds:List<Order>, private val eventMyOrderItemListening: EventMyOrderItemListening): RecyclerView.Adapter<RvAdapterOrderItem.OrderItemViewHolder>() {
    private val dbRefOrderDetail: DatabaseReference = FirebaseDatabase.getInstance().getReference("OrderDetails")
    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy")

    class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_order_item, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val txtOrderId = holder.itemView.findViewById<TextView>(R.id.txtOrderId)
        val txtCreateTime = holder.itemView.findViewById<TextView>(R.id.txtCreateTime)
        val txtQuantityValue = holder.itemView.findViewById<TextView>(R.id.txtQuantityValue)
        val txtTotalAmountValue = holder.itemView.findViewById<TextView>(R.id.txtTotalAmountValue)
        val txtOrderStatus = holder.itemView.findViewById<TextView>(R.id.txtOrderStatus)
        val btnDetails = holder.itemView.findViewById<Button>(R.id.btnDetails)

        holder.itemView.apply {
            txtOrderId.text = "Order ${ds[position].orderId}"
            txtCreateTime.text = dateFormat.format(ds[position].createTime)
            txtTotalAmountValue.text = "${ds[position].totalPrice}$"
            txtOrderStatus.text = ds[position].status

            if (ds[position].status == "Delivered")
                txtOrderStatus.setTextColor(Color.parseColor("#2AA952"))
            else if (ds[position].status == "Cancelled")
                txtOrderStatus.setTextColor(resources.getColor(R.color.text_color_hint))
            else
                txtOrderStatus.setTextColor(resources.getColor(R.color.red_button))

            dbRefOrderDetail.get().addOnSuccessListener {
                if (it.exists()){
                    var quantityValue = 0
                    for (orderDetailSnap in it.children) {
                        if (orderDetailSnap.child("orderId").value.toString() == ds[position].orderId)
                            quantityValue += orderDetailSnap.child("quantity").value.toString().toInt()
                    }
                    txtQuantityValue.text = quantityValue.toString()
                }
            }

            btnDetails.setOnClickListener {
                eventMyOrderItemListening.onClickDetailsItemListening(position)
            }
        }
    }
}