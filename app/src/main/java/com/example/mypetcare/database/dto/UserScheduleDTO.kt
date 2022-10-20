package com.example.mypetcare.database.dto

data class UserScheduleDTO(
    var uid: String? = null,
    var selectedCategory: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var request: String = "",
    var managerUid: String? = null,
    var managerName: String? = null,
    var registrationTime: String = "",

)
