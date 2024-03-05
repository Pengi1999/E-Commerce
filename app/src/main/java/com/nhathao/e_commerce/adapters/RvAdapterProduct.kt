package com.nhathao.e_commerce.adapters

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nhathao.e_commerce.Interfaces.EventProductItemListening
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.models.Product

class RvAdapterProduct (private var dsProduct:List<Product>, private var layout:Int, private val eventProductItemListening: EventProductItemListening) : RecyclerView.Adapter<RvAdapterProduct.ProductViewHolder>(){

    class ProductViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dsProduct.size
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor", "UseCompatLoadingForDrawables")
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
            if(dsProduct[position].productImage != ""){
                val bytes = Base64.decode(dsProduct[position].productImage, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imgProduct.setImageBitmap(bitmap)
            }
            when (dsProduct[position].productMode) {
                "NEW" -> {
                    txtProductMode.setBackgroundResource(R.drawable.bg_new)
                    txtProductMode.visibility = View.VISIBLE
                }
                "" -> {
                    txtProductMode.visibility = View.INVISIBLE
                }
                else -> {
                    txtProductMode.setBackgroundResource(R.drawable.bg_sale)
                    txtProductMode.visibility = View.VISIBLE
                    val saleValue = dsProduct[position].productMode.substring(1,3).toFloat() / 100
                    txtProductPrice.setTextColor(R.color.text_color_hint)
                    txtProductPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    txtProductPriceSale.text = "${dsProduct[position].productPrice - (dsProduct[position].productPrice * saleValue).toInt()}$"
                }
            }
            txtProductPrice.text = "${dsProduct[position].productPrice}$"
            txtProductMode.text = dsProduct[position].productMode
            txtProductDes.text = dsProduct[position].productDescribe
            ratingProduct.rating = dsProduct[position].productRating!!
            txtRatingQuantity.text = "(${dsProduct[position].productRatingQuantity})"
            txtProductName.text = dsProduct[position].productName

            btnFavorite.setOnClickListener {
//                if (btnFavorite.drawable.toBitmap().sameAs(resources.getDrawable(R.drawable.ic_favorite_filled_18).toBitmap()))
//                    btnFavorite.setImageResource(R.drawable.ic_favorite_gray)
//                else {
//                    btnFavorite.setImageResource(R.drawable.ic_favorite_filled_18)
//                }
                eventProductItemListening.onClickBtnFavoriteItem(position)
            }

            holder.itemView.setOnClickListener {
                eventProductItemListening.OnItemClick(position)
            }

            holder.itemView.setOnLongClickListener {
                eventProductItemListening.OnItemLongClick(position)
                true
            }
        }
    }
}