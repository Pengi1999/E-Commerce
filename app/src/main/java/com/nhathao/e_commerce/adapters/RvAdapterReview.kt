package com.nhathao.e_commerce.adapters

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.models.Review
import com.nhathao.e_commerce.models.User
import java.text.SimpleDateFormat

class RvAdapterReview (private var ds:List<Review>): RecyclerView.Adapter<RvAdapterReview.ReviewViewHolder>() {
    private val dbRefUser: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private val dbRefRating: DatabaseReference = FirebaseDatabase.getInstance().getReference("Ratings")
    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("kk:mm a\nMMMM d, yyyy")

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_review_item, parent, false)
        return ReviewViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val imgAvatar = holder.itemView.findViewById<ImageView>(R.id.imgAvatar)
        val txtUserName = holder.itemView.findViewById<TextView>(R.id.txtUserName)
        val ratingProductByUser = holder.itemView.findViewById<RatingBar>(R.id.ratingProductByUser)
        val txtReviewDate = holder.itemView.findViewById<TextView>(R.id.txtReviewDate)
        val txtReviewContent = holder.itemView.findViewById<TextView>(R.id.txtReviewContent)

        holder.itemView.apply {
            //Xu Ly Image Avatar
            dbRefUser.child(ds[position].userAccountName).get().addOnSuccessListener {
                if (it.exists()){
                    val user = User(
                        it.child("userAccountName").value.toString(),
                        it.child("userPWD").value.toString(),
                        it.child("userName").value.toString(),
                        it.child("secretCode").value.toString(),
                        it.child("birthday").value.toString(),
                        it.child("avatar").value.toString(),
                        it.child("email").value.toString(),
                        it.child("typeAccount").value.toString()
                    )
                    val bytes = Base64.decode(user.avatar, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    imgAvatar.setImageBitmap(bitmap)
                    txtUserName.text = user.userName
                }
            }
            dbRefRating.get().addOnSuccessListener {
                if (it.exists()){
                    for (snapRating in it.children){
                        if (snapRating.child("userAccountName").value.toString() == ds[position].userAccountName &&
                            snapRating.child("productId").value.toString() == ds[position].productId){
                            ratingProductByUser.rating = snapRating.child("ratingStar").value.toString().toFloat()
                            break
                        }
                    }
                }
            }
            txtReviewDate.text = dateFormat.format(ds[position].reviewDate)
            txtReviewContent.text = ds[position].reviewContent
        }
    }
}