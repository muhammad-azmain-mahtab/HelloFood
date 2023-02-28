package com.example.hellofood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderHistoryAdapter(var orderHistoryItems : List<OrderHistoryItem>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    inner class OrderHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var orderDate = itemView.findViewById<TextView>(R.id.tv_orderDate)
        var orderPrice = itemView.findViewById<TextView>(R.id.tv_orderPrice)
        var orderStatus = itemView.findViewById<TextView>(R.id.tv_orderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false)
        return OrderHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        holder.orderDate.text = orderHistoryItems[position].date
        holder.orderPrice.text = orderHistoryItems[position].price
        holder.orderStatus.text = orderHistoryItems[position].status
    }

    override fun getItemCount(): Int {
        return orderHistoryItems.size
    }
}