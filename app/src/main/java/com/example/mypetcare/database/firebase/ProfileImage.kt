package com.example.mypetcare.database.firebase

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.mypetcare.R
import com.example.mypetcare.database.Cache
import com.example.mypetcare.database.constant.UserInfoConstants
import com.google.firebase.storage.FirebaseStorage


class ProfileImage(activity: Activity, uid: String) {

    private val mActivity = activity
    private val storage = FirebaseStorage.getInstance()
    /*
    * 참조 생성
    * 파일 업로드, 다운로드, 삭제, 메타데이터 가져오기 또는 업데이트를 하려면 참조를 만든다.
    * 참조는 클라우드의 파일을 가리키는 포인터이다.
    * 참조는 메모리에 부담을 주지 않으므로 원하는 만큼 만들 수 있으며 여러 작업에서 재사용할 수도 있다.
    */
    private val storageRef = storage.reference
    private val filePath = UserInfoConstants.PROFILE_IMAGE_PATH
    private val fileName = "${uid}.png"

    // 저장된 프로필 이미지 파일 찾기
    fun getProfileImage(imageView: ImageView) {
        val file = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/${filePath}")

        // file 에서 디렉토리 확인
        // 만약 없다면 디렉토리를 생성
        if( !file!!.isDirectory ) {
            file.mkdir()
        }
        downloadProfileImage(imageView)
    }

    // 프로필 이미지 다운로드
    private fun downloadProfileImage(imageView: ImageView) {
        storageRef  .child("${filePath}/")
                    .child(fileName).downloadUrl
                    .addOnSuccessListener { uri ->
                        println("사진 다운로드 성공 uri: $uri")

                        /*
                         * context 로 호출하면 아래와 같은 에러 발생
                         * java.lang.NoClassDefFoundError: Failed resolution of: Landroid/support/v4/app/FragmentActivity;
                         */
                        // context X -> activity
                        Glide.with(mActivity).load(uri).asBitmap().into(imageView)
                    }
                    .addOnFailureListener {
                        imageView.setImageResource(R.drawable.basic_profile_image)
                    }
    }

    // firebaseStorage 에 기본 프로필 이미지 저장
    // 이미지 리소스 drawable -> uri 로 변환
    fun setBasicProfileImage(context: Context) {
        val drawableId = R.drawable.basic_profile_image
        val imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                                    + "://" + context.resources.getResourcePackageName(drawableId)
                                    + '/' + context.resources.getResourceTypeName(drawableId)
                                    + '/' + context.resources.getResourceEntryName(drawableId)
                                )
        println("imageUri: $imageUri")
        uploadProfileImageToFirebase(imageUri)
        // 캐시 파일 생성
        Cache(mActivity, fileName).createCache(drawableId)
    }

    // firebaseStorage 에 기본 프로필 이미지 저장
    fun uploadProfileImageToFirebase(uri: Uri) {
        val imagesRef = storageRef.child("${filePath}/").child(fileName)
        val uploadTask = imagesRef.putFile(uri)
        uploadTask  .addOnSuccessListener {
                        println("ProfileImage, 사진 업로드 성공")
                    }
                    .addOnFailureListener {
                        println("ProfileImage, 사진 업로드 실패 -> ${it.message}")
                        //E/StorageException: The server has terminated the upload session 해결
                    }
    }

    // 기존 저장된 프로필 이미지 삭제
    fun deleteProfileImageFromFirebase() {
        val deleteImagesRef = storageRef.child("${filePath}/").child(fileName)
        deleteImagesRef .delete()
                        .addOnSuccessListener {
                            println("사진 삭제 성공")
                        }
                        .addOnFailureListener {
                            if( it.message == "Object does not exist at location." )
                                println("기존 저장된 이미지가 없습니다.")
                            else
                                println("사진 삭제 실패 -> ${it.message}")
                        }
    }

}