package com.nhathao.e_commerce.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nhathao.e_commerce.Interfaces.RvCategoryInterface
import com.nhathao.e_commerce.R

class RvAdapterCategory (private var ds:List<String>, val onCategoryClick:RvCategoryInterface): RecyclerView.Adapter<RvAdapterCategory.CategoryViewHolder>() {
    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val txtCategory = holder.itemView.findViewById<TextView>(R.id.txtCategory)
        holder.itemView.apply {
            txtCategory.text = ds[position]

            holder.itemView.setOnClickListener {
                onCategoryClick.OnClickCategory(position)
            }
        }
    }
}