package com.example.mypetcare

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager

// 화면 바깥 터치 시 키보드 내리기
class HideKeyboard {

    // dialog, activity 에서
    fun hideKeyboard(focusView: View, context: Context, event: MotionEvent) {
        val rect = Rect()
        focusView.getGlobalVisibleRect(rect)
        val x = event.x.toInt()
        val y = event.y.toInt()

        if (!rect.contains(x, y)) {
            val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(focusView.windowToken, 0)
            focusView.clearFocus()
        }
    }

    // chat activity 에서
    fun hideKeyboard(activity: Activity) {
        val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
    }

    // DialogFragment 에서 : dispatchTouchEvent override 가 안됨.
    fun hideKeyboardInDialogFragment(context: Context, view: View) {

        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}