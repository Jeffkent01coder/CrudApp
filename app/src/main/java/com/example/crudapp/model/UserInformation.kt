package com.example.crudapp.model

data class UserInformation(
    val uid: String,
    val firstName: String? = "",
    val lastName: String? = "",
    val age: String? = "",
    val town: String? = "",
    val gender: String? = ""
)
