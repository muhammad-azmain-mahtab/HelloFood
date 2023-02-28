package com.example.hellofood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FoodItemAdapter(var foodItems: List<FoodItem>, val listener : OnItemClickListener) :
    RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder>() {

    inner class FoodItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var foodTitle = itemView.findViewById<TextView>(R.id.tv_foodName)
        var foodPrice = itemView.findViewById<TextView>(R.id.tv_foodPrice)
        var foodImage = itemView.findViewById<ImageView>(R.id.img_foodImage)
        var foodQuantity = itemView.findViewById<TextView>(R.id.tv_quantity)
        var foodButton = itemView.findViewById<TextView>(R.id.btn_addToCart)
        var plusButton = itemView.findViewById<ImageView>(R.id.img_plus)
        var minusButton = itemView.findViewById<ImageView>(R.id.img_minus)

        init {
            foodButton.setOnClickListener {
                val position = bindingAdapterPosition
                listener.onFoodItemClick(position)
            }
            plusButton.setOnClickListener {
                val position = bindingAdapterPosition
                notifyItemChanged(position)
                listener.onPlusClick(position)
            }
            minusButton.setOnClickListener {
                val position = bindingAdapterPosition
                notifyItemChanged(position)
                listener.onMinusClick(position)
            }
        }

        override fun onClick(p0: View?) {

        }

    }

    interface OnItemClickListener {
        fun onFoodItemClick(position: Int)
        fun onPlusClick(position: Int)
        fun onMinusClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_food_item, parent, false)
        return FoodItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        holder.foodTitle.text = foodItems[position].title
        holder.foodPrice.text = foodItems[position].price
        Glide.with(holder.foodImage.getContext()).load(foodItems[position].imageUrl)
            .into(holder.foodImage);
        holder.foodQuantity.text = foodItems[position].quantity
    }

    override fun getItemCount(): Int {
        return foodItems.size
    }
}