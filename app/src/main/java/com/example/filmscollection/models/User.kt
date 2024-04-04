package com.example.filmscollection.models

class User(
    val email: String = "",
    val name: String = "",
    val surname: String = "",
    val nickname: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val city: String = "",
    val country: String = "",
    val age: Int = 0,
    val gender: String = "",
    val favorites: MutableList<Int> = mutableListOf()
)