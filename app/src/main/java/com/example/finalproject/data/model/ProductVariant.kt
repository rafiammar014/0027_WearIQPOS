package com.example.finalproject.data.model

import com.google.gson.annotations.SerializedName

data class ProductVariant(
    @SerializedName("id")
    val id: String,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("sku")
    val sku: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("size")
    val size: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("stock")
    val stock: Int,
    @SerializedName("image_url")
    val imageUrl: String? = null
)
