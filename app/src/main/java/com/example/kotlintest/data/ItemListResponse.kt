package com.example.kotlintest.data

data class ItemListResponse(
    var total: Int,
    val items: MutableList<ItemDetailResponse>
)

data class ItemDetailResponse(
    val id: Int,
    val brand: String,
    val name: String,
    val image: String
)