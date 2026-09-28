package com.rentaya.app.data.model

data class Message(
    val id: String,
    val propertyId: String,
    val propertyTitle: String,
    val landlordName: String,
    val lastMessage: String,
    val timestamp: Long,
    val unread: Boolean = false
)

data class ChatMessage(
    val id: String,
    val text: String,
    val timestamp: Long,
    val isFromMe: Boolean
)
