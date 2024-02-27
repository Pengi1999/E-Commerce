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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.models.Bag
import com.nhathao.e_commerce.models.Product
import com.nhathao.e_commerce.models.Quantity
import com.nhathao.e_commerce.models.Review
import com.nhathao.e_commerce.models.User
import java.text.SimpleDateFormat

class RvAdapterBagItem (private var ds:List<Bag>): RecyclerView.Adapter<RvAdapterBagItem.BagItemViewHolder>() {
    private val dbRefBag: DatabaseReference = FirebaseDatabase.getInstance().getReference("Bags")
    private val dbRefProduct: DatabaseReference = FirebaseDatabase.getInstance().getReference("Products")
    private val dbRefQuantity: DatabaseReference = FirebaseDatabase.getInstance().getReference("Quantities")
    private lateinit var quantity: Quantity
    private lateinit var product: Product

    class BagItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BagItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_bag_item, parent, false)
        return BagItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: BagItemViewHolder, position: Int) {
        val imgProduct = holder.itemView.findViewById<ImageView>(R.id.imgProduct)
        val txtProductName = holder.itemView.findViewById<TextView>(R.id.txtProductName)
        val txtSelectedColor = holder.itemView.findViewById<TextView>(R.id.txtSelectedColor)
        val txtSelectedSize = holder.itemView.findViewById<TextView>(R.id.txtSelectedSize)
        val txtQuantity = holder.itemView.findViewById<TextView>(R.id.txtQuantity)
        val txtProductPrice = holder.itemView.findViewById<TextView>(R.id.txtProductPrice)
        val txtProductPriceSale = holder.itemView.findViewById<TextView>(R.id.txtProductPriceSale)
        val btnIncrease = holder.itemView.findViewById<ImageButton>(R.id.btnIncrease)
        val btnDecrease = holder.itemView.findViewById<ImageButton>(R.id.btnDecrease)

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
            txtQuantity.text = ds[position].quantity.toString()

            btnIncrease.setOnClickListener {
                if (ds[position].quantity + 1 > quantity.quantity) {
                    Toast.makeText(context, "The maximum quantity of this product has been reached", Toast.LENGTH_SHORT).show()
                }
                else {
                    ds[position].quantity += 1
                    dbRefBag.child(ds[position].bagId).setValue(ds[position])
                }
            }

            btnDecrease.setOnClickListener {
                if (ds[position].quantity > 1) {
                    ds[position].quantity -= 1
                    dbRefBag.child(ds[position].bagId).setValue(ds[position])
                }
                else {
                    dbRefBag.child(ds[position].bagId).removeValue()
                }
            }
        }
    }
}