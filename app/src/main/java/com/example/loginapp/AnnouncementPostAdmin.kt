package com.example.loginapp

import com.google.firebase.Timestamp

data class AnnouncementPostAdmin(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val timestamp: Timestamp? = null
)
