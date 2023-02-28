package com.example.hellofood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewOrderAdapter(var cartItems: List<CartItem>) :
    RecyclerView.Adapter<ReviewOrderAdapter.ReviewOrderViewHolder>() {

    inner class ReviewOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var foodTitle = itemView.findViewById<TextView>(R.id.tv_reviewOrderName)
        var foodPrice = itemView.findViewById<TextView>(R.id.tv_reviewOrderPrice)
        var quantity = itemView.findViewById<TextView>(R.id.tv_reviewOrderQuantity)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewOrderViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_review_order, parent, false)
        return ReviewOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewOrderViewHolder, position: Int) {
        holder.foodTitle.text = cartItems[position].title
        holder.foodPrice.text = cartItems[position].price
        holder.quantity.text = cartItems[position].quantity
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}