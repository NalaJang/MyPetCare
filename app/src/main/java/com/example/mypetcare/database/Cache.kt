package com.example.mypetcare.database

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

class Cache(activity: Activity, fileName: String) {
    private val mActivity = activity
    private val mFileName = fileName

    // 캐시 파일 최초 생성
    fun createCache(resource: Int) {
        val bitmap = BitmapFactory.decodeResource(mActivity.resources, resource)
        saveImageToCache(bitmap)
    }

    // 내부 저장소에 이미지 파일을 bitmap 으로 저장
    fun saveImageToCache(bitmap: Bitmap) {
        val storage = mActivity.cacheDir
        val file = File(storage, mFileName)

        try {
            file.createNewFile()

            // 파일을 사용할 수 있도록 스트림 준비
            val outputStream = FileOutputStream(file)

            // compress 함수를 사용해 스트림에 비트맵을 저장
            // 100: 원본 화질 그래도 사진 압축 -> 파일을 가져오는 속도를 더 빠르게 하고 싶다면 화질 저하를 추천
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
            println("saveImageToCache 성공: ${file.path}")

        } catch (e: FileNotFoundException) {
            println("FileNotFoundException: ${e.message}")
        } catch (e: IOException) {
            println("IOException: ${e.message}")
        }
    }

    // cache 에서 bitmap 이미지 가져오기
    fun getImageFromCache(imageView: ImageView): Boolean {
        val filePath = "${mActivity.cacheDir}/"
        val imagePath = "${filePath}${mFileName}"
        try {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            if( bitmap == null ) {
                println("null")
                return false
            }

            imageView.setImageBitmap(bitmap)
        } catch (e: FileNotFoundException) {
            println("FileNotFoundException")
        }

        return true
    }
}