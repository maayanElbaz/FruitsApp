package com.example.fruitsapp

data class FruitsListModel(
    val err: Int,
    val fruits: ArrayList<Fruit>
)

data class Fruit(
    val description: String,
    val image: String,
    val name: String,
    val price: Int
)