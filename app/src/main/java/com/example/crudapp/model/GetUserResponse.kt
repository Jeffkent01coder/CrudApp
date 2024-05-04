package com.example.crudapp.model

data class GetUserResponse(
    val id: String? = "",
    val first_name: String? = "",
    val last_name: String? = "",
    val email: String? = "",
    val age: String? = "",
    val town: String? = "",
    val gender: String? = ""
)
