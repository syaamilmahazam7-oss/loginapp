package com.example.loginapp.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.Timestamp

data class ItemModel(
    val name: String = "",
    val description: String = "",
    val downloadUrl: String = "",
    val badge: String = "",
    @get:Exclude var id: String = ""
)

data class TutorialModel(
    val title: String = "",
    val description: String = "",
    val videoUrl: String = "",
    val order: Long = 0,
    @get:Exclude var id: String = ""
)

data class AnnouncementModel(
    val title: String = "",
    val body: String = "",
    val timestamp: Timestamp? = null,
    @get:Exclude var id: String = ""
)

data class ChatSummary(
    val chatId: String = "",
    val memberCode: String = "",
    val lastMessage: String = "",
    val updatedAt: Timestamp? = null
)

data class ChatMessage(
    val senderUid: String = "",
    val text: String = "",
    val timestamp: Timestamp? = null
)
