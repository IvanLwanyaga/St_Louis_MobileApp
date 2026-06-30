package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class FeeSummary(
    @SerializedName("total_due")
    val totalDue: Double,
    @SerializedName("total_paid")
    val totalPaid: Double,
    @SerializedName("balance")
    val balance: Double,
    @SerializedName("status")
    val status: String // "fully_paid", "partial", "unpaid"
)

data class Payment(
    @SerializedName("id")
    val id: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("receipt_number")
    val receiptNumber: String,
    @SerializedName("status")
    val status: String // "completed", "pending"
)

data class FeeDetailsResponse(
    @SerializedName("summary")
    val summary: FeeSummary,
    @SerializedName("history")
    val history: List<Payment>
)
