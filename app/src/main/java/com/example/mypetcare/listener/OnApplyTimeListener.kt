package com.example.mypetcare.listener

interface OnApplyTimeListener {
    fun setOnStartTime(selectedHour: Int, selectedMinute: Int)
    fun setOnEndTime(selectedHour: Int, selectedMinute: Int)
}