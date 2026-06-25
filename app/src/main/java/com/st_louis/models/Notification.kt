package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("is_read")
    val isRead: Boolean,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String? = null,

    @SerializedName("link")
    val link: String? = null
)