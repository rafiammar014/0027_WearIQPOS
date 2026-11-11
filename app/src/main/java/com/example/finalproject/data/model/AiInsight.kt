package com.example.finalproject.data.model

import com.google.gson.annotations.SerializedName

data class AiInsight(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("recommended_action")
    val recommendedAction: String,
    @SerializedName("confidence_score")
    val confidenceScore: Double,
    @SerializedName("generated_at")
    val generatedAt: String
)
