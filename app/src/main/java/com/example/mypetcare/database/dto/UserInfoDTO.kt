package com.example.mypetcare.database.dto

data class UserInfoDTO(
    var uid: String? = null,
    var userEmail: String? = null,
    var userPassword: String? = null,
    var userName: String? = "",
    var userPhoneNum: String? = "",
    var userPetName: String? = "",
    var userPetAge: String? = null,
    var userPetSpecies: String? = null,
    var userPetWeight: String? = "",
    var userPetCharacter: String? = null,
)
