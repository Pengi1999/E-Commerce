package com.nhathao.e_commerce.adapters

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.models.Product

class RvAdapterProduct (private var ds:List<Product>, private var layout:Int) : RecyclerView.Adapter<RvAdapterProduct.ProductViewHolder>(){

    class ProductViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val imgProduct = holder.itemView.findViewById<ImageView>(R.id.imgProduct)
        val ratingProduct = holder.itemView.findViewById<RatingBar>(R.id.ratingProduct)
        val txtProductMode = holder.itemView.findViewById<TextView>(R.id.txtProductMode)
        val txtRatingQuantity = holder.itemView.findViewById<TextView>(R.id.txtRatingQuantity)
        val txtProductDes = holder.itemView.findViewById<TextView>(R.id.txtProductDes)
        val txtProductName = holder.itemView.findViewById<TextView>(R.id.txtProductName)
        val txtProductPrice = holder.itemView.findViewById<TextView>(R.id.txtProductPrice)
        val txtProductPriceSale = holder.itemView.findViewById<TextView>(R.id.txtProductPriceSale)
        val btnFavorite = holder.itemView.findViewById<ImageButton>(R.id.btnFavorite)

        holder.itemView.apply {
            if(ds[position].productImage != ""){
                val bytes = Base64.decode(ds[position].productImage, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imgProduct.setImageBitmap(bitmap)
            }
            txtProductMode.text = ds[position].productMode
            if (ds[position].productMode == "NEW"){
                txtProductMode.setBackgroundResource(R.drawable.bg_new)
                txtProductPrice.text = "${ds[position].productPrice}$"
            }
            else {
                txtProductMode.setBackgroundResource(R.drawable.bg_sale)
                val saleValue = ds[position].productMode.substring(1,3).toFloat() / 100
                txtProductPrice.text = "${ds[position].productPrice}$"
                txtProductPrice.setTextColor(R.color.text_color_hint)
                txtProductPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                txtProductPriceSale.text = "${ds[position].productPrice - (ds[position].productPrice * saleValue).toInt()}$"
            }
            txtProductDes.text = ds[position].productDescribe
            ratingProduct.rating = ds[position].productRating!!
            txtRatingQuantity.text = "(${ds[position].productRatingQuantity})"
            txtProductName.text = ds[position].productName

            btnFavorite.setOnClickListener {

            }
        }
    }
}