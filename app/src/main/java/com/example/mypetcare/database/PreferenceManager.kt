package com.example.mypetcare.database

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager {

    val PREFERENCES_NAME = "rebuild_preference"
    val DEFAULT_VALUE_STRING = "";

    //  SharedPreferences 간단한 설정 값이나 문자열등의 데이터를 파일로 저장. 앱 폴더 내에 저장되므로 앱을 삭제하면 데이터도 삭제됨
    //  데이터를 로드 할 때 저장에 사용했던 key 값을 이용. key 에 해당되는 데이터가 없다면, default 값으로 호출됨
    private fun getPreferences(context: Context): SharedPreferences? {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun setString(context: Context, key: String, value: String) {
        val prefs = getPreferences(context)
        val editor = prefs?.edit()
        editor?.putString(key, value)
        editor?.commit()
    }

    fun getString(context: Context, key: String): String? {
        val prefs = getPreferences(context)
        val value = prefs?.getString(key, DEFAULT_VALUE_STRING)
        return value
    }
}