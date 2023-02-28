package com.example.hellofood

data class FoodItem(
    var title: String ?= null,
    var price: String ?= null,
    val imageUrl: String ?= null,
    var quantity: String ?= null
)
