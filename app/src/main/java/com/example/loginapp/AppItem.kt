package com.example.loginapp

data class AppItem(
    val id: String = "",
    val name: String = "",
    val packageName: String = "",
    val description: String = "",
    val downloadUrl: String = "",
    val videoUrl: String = "",
    val requiredRole: String = "user"
)
