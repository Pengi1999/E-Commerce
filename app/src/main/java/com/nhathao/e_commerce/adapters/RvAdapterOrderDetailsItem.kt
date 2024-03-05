package com.nhathao.e_commerce.adapters

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.models.OrderDetail
import com.nhathao.e_commerce.models.Product
import com.nhathao.e_commerce.models.Quantity

class RvAdapterOrderDetailsItem (private var ds:List<OrderDetail>): RecyclerView.Adapter<RvAdapterOrderDetailsItem.OrderDetailsItemViewHolder>() {
    private val dbRefProduct: DatabaseReference = FirebaseDatabase.getInstance().getReference("Products")
    private val dbRefQuantity: DatabaseReference = FirebaseDatabase.getInstance().getReference("Quantities")
    private lateinit var quantity: Quantity
    private lateinit var product: Product

    class OrderDetailsItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_orderdetails_item, parent, false)
        return OrderDetailsItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onBindViewHolder(holder: OrderDetailsItemViewHolder, position: Int) {
        val imgProduct = holder.itemView.findViewById<ImageView>(R.id.imgProduct)
        val txtProductName = holder.itemView.findViewById<TextView>(R.id.txtProductName)
        val txtProductDes = holder.itemView.findViewById<TextView>(R.id.txtProductDes)
        val txtSelectedColor = holder.itemView.findViewById<TextView>(R.id.txtSelectedColor)
        val txtSelectedSize = holder.itemView.findViewById<TextView>(R.id.txtSelectedSize)
        val txtUnitsValue = holder.itemView.findViewById<TextView>(R.id.txtUnitsValue)
        val txtProductPrice = holder.itemView.findViewById<TextView>(R.id.txtProductPrice)
        val txtProductPriceSale = holder.itemView.findViewById<TextView>(R.id.txtProductPriceSale)

        holder.itemView.apply {
            dbRefQuantity.child(ds[position].quantityId).get().addOnSuccessListener { it ->
                if (it.exists()){
                    quantity = Quantity(
                        it.child("quantityId").value.toString(),
                        it.child("productId").value.toString(),
                        it.child("productColor").value.toString(),
                        it.child("productSize").value.toString(),
                        it.child("quantity").value.toString().toInt()
                    )
                    txtSelectedColor.text = quantity.productColor
                    txtSelectedSize.text = quantity.productSize
                    dbRefProduct.child(quantity.productId).get().addOnSuccessListener {
                        if (it.exists()){
                            product = Product(
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
                            val bytes = Base64.decode(product.productImage, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            imgProduct.setImageBitmap(bitmap)
                            txtProductName.text = product.productName
                            txtProductDes.text = product.productDescribe
                            txtProductPrice.text = "${product.productPrice}$"
                            if (product.productMode != "" && product.productMode != "NEW"){
                                val saleValue = product.productMode.substring(1,3).toFloat() / 100
                                txtProductPrice.setTextColor(R.color.text_color_hint)
                                txtProductPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                                txtProductPriceSale.text = "${product.productPrice - (product.productPrice * saleValue).toInt()}$"
                            }
                        }
                    }
                }
            }
            txtUnitsValue.text = ds[position].quantity.toString()
        }
    }
}