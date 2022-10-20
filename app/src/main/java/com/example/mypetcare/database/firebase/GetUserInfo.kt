package com.example.mypetcare.database.firebase

import android.content.Context
import android.widget.TextView
import com.example.mypetcare.database.PreferenceManager
import com.example.mypetcare.database.constant.UserInfoConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GetUserInfo {
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    // 사용자 정보 가져오기
    fun getUserInfo(userName: TextView, userPhoneNum: TextView,
                    petName: TextView, petAge: TextView, petWeight: TextView,
                    petSpecies: TextView, petCharacter: TextView, context: Context) {

        db  .collection(UserInfoConstants.USER_INFO)
            .get()
            .addOnCompleteListener { task ->
                if( task.isSuccessful ) {

                    for( i in task.result!! ) {
                        if( i.id == uid.toString() ) {

                            // db 필드 데이터
                            val myName         = i.data[UserInfoConstants.USER_NAME]
                            val myPhoneNum     = i.data[UserInfoConstants.USER_PHONE_NUM]
                            val myPetName      = i.data[UserInfoConstants.PET_NAME]
                            val myPetAGE       = i.data[UserInfoConstants.PET_AGE]
                            val myPetWeight    = i.data[UserInfoConstants.PET_WEIGHT]
                            val myPetSpecies   = i.data[UserInfoConstants.PET_SPECIES]
                            val myPetCharacter = i.data[UserInfoConstants.PET_CHARACTER]

                            userName.text     = myName.toString()
                            userPhoneNum.text = myPhoneNum.toString()
                            petName.text      = myPetName.toString()
                            petAge.text       = myPetAGE.toString()
                            petWeight.text    = myPetWeight.toString()
                            petSpecies.text   = myPetSpecies.toString()
                            petCharacter.text = myPetCharacter.toString()

                            PreferenceManager().setString(
                                context,
                                UserInfoConstants.USER_NAME,
                                myName.toString()
                            )

                            break
                        }
                    }
                } else
                    println("No such document")
            }
            .addOnFailureListener { e ->
                println("실패 >> ${e.message}")
            }
    }
}