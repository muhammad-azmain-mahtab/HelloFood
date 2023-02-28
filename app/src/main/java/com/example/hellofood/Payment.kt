package com.example.hellofood

data class Payment(
    val uid: String? = null,
    val price: String? = null,
    val date: String? = null,
    var status: String ?= null,
    var orderId: String? = null
)

