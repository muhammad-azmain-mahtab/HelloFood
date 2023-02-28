package com.example.hellofood

data class Shop(
    var title: String ?= null,
    var category: String ?= null,
    var price: String ?= null,
    val imageUrl: String ?= null,
    val locationLat: String? = null,
    val locationLong: String? = null
)
