package com.example.hellofood

data class CartItem(
    var title: String ?= null,
    var price: String ?= null,
    var imageUrl: String ?= null,
    var quantity: String ?= null,
    var shop: String?= null
)
