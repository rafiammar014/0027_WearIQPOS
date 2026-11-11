package com.example.finalproject.data.model

import com.google.gson.annotations.SerializedName

data class Transaction(
    @SerializedName("id")
    val id: String,
    @SerializedName("invoice_number")
    val invoiceNumber: String,
    @SerializedName("cashier_id")
    val cashierId: String,
    @SerializedName("total_amount")
    val totalAmount: Double,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("items")
    val items: List<TransactionItem> = emptyList()
)

data class TransactionItem(
    @SerializedName("variant_id")
    val variantId: String,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("unit_price")
    val unitPrice: Double,
    @SerializedName("subtotal")
    val subtotal: Double
)
