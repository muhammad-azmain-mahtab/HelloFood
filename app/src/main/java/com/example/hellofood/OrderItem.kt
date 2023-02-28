package com.example.hellofood

data class OrderItem(
    var title: String? = null,
    var price: String? = null,
    var quantity: String? = null,
    var status: String? = null,
    var date: String? = null,
    var receiveTime: String? = null,
    var orderType: String? = null,
    var orderId: String? = null
)