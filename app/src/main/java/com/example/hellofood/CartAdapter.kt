package com.example.hellofood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(var cartItems: List<CartItem>, val listener : OnItemClickListener) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var foodTitle = itemView.findViewById<TextView>(R.id.tv_cartName)
        var foodPrice = itemView.findViewById<TextView>(R.id.tv_cartPrice)
        var foodImage = itemView.findViewById<ImageView>(R.id.img_cartImage)
        var quantity = itemView.findViewById<TextView>(R.id.tv_cartQuantity)
        var plusButton = itemView.findViewById<ImageView>(R.id.img_cartPlus)
        var minusButton = itemView.findViewById<ImageView>(R.id.img_cartMinus)

        init {
            plusButton.setOnClickListener {
                val position = bindingAdapterPosition
                //notifyItemChanged(position)
                listener.onPlusItemClick(position)
            }
            minusButton.setOnClickListener {
                val position = bindingAdapterPosition
                //notifyItemChanged(position)
                listener.onMinusItemClick(position)
            }
        }

        override fun onClick(p0: View?) {

        }
    }

    interface OnItemClickListener {
        fun onPlusItemClick(position: Int)
        fun onMinusItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.foodTitle.text = cartItems[position].title
        holder.foodPrice.text = cartItems[position].price
        holder.quantity.text = cartItems[position].quantity
        Glide.with(holder.foodImage.getContext()).load(cartItems[position].imageUrl)
            .into(holder.foodImage);
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}