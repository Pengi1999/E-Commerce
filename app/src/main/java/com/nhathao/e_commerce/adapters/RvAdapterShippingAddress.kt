package com.nhathao.e_commerce.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.models.ShippingAddress

class RvAdapterShippingAddress (private var ds:List<ShippingAddress>): RecyclerView.Adapter<RvAdapterShippingAddress.ShippingAddressViewHolder>() {

    class ShippingAddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShippingAddressViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_shipping_address_item, parent, false)
        return ShippingAddressViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: ShippingAddressViewHolder, position: Int) {
        val txtFullName = holder.itemView.findViewById<TextView>(R.id.txtFullName)
        val txtAddress = holder.itemView.findViewById<TextView>(R.id.txtAddress)
        val txtAddress2 = holder.itemView.findViewById<TextView>(R.id.txtAddress2)
        val txtEdit = holder.itemView.findViewById<TextView>(R.id.txtEdit)
        val chkUsedAddress = holder.itemView.findViewById<CheckBox>(R.id.chkUsedAddress)

        holder.itemView.apply {
            txtFullName.text = ds[position].fullName
            txtAddress.text = ds[position].address
            txtAddress2.text = "${ds[position].city}, ${ds[position].region} ${ds[position].zipCode}, ${ds[position].country}"
            if(ds[position].isUsed == true)
                chkUsedAddress.isChecked = true
        }
    }
}