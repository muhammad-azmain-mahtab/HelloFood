package com.example.hellofood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ShopAdapter(var shops: List<Shop>, val listener : OnItemClickListener) :
    RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {

    inner class ShopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var shopTitle = itemView.findViewById<TextView>(R.id.tv_shopName)
        var shopCuisine = itemView.findViewById<TextView>(R.id.tv_shopCuisine)
        var shopPrice = itemView.findViewById<TextView>(R.id.tv_shopPrice)
        var shopImage = itemView.findViewById<ImageView>(R.id.img_shopImage)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = bindingAdapterPosition
            listener.onShopItemClick(position)
        }
    }

    interface OnItemClickListener {
        fun onShopItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_shop, parent, false)
        return ShopViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.shopTitle.text = shops[position].title
        holder.shopCuisine.text = shops[position].category
        holder.shopPrice.text = shops[position].price
        Glide.with(holder.shopImage.getContext()).load(shops[position].imageUrl)
            .into(holder.shopImage);
    }

    override fun getItemCount(): Int {
        return shops.size
    }
}