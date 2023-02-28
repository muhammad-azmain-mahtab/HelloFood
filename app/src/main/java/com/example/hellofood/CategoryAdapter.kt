package com.example.hellofood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(var categories: List<Category>, val listener: OnItemClickListener) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var categoryTitle = itemView.findViewById<TextView>(R.id.tv_title)
        var categoryImage = itemView.findViewById<ImageView>(R.id.img_title)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = bindingAdapterPosition
            listener.onCategoryItemClick(position)
        }
    }

    interface OnItemClickListener {
        fun onCategoryItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.categoryTitle.text = categories[position].title
        holder.categoryImage.setImageResource(categories[position].image)
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}