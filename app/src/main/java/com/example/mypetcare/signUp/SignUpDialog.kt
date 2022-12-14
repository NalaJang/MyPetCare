package com.example.mypetcare.signUp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import com.example.mypetcare.HideKeyboard
import com.example.mypetcare.R
import com.example.mypetcare.database.constant.UserInfoConstants
import com.example.mypetcare.databinding.DialogSignUpBinding
import com.example.mypetcare.database.dto.UserInfoDTO
import com.example.mypetcare.database.firebase.ProfileImage
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception

@SuppressLint("ResourceType")
class SignUpDialog constructor(activity: Activity): Dialog(activity, R.drawable.dialog_full_screen)
    , View.OnClickListener, TextWatcher {

    private val mActivity = activity
    private var mBinding: DialogSignUpBinding? = null
    private val binding get() = mBinding!!
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    private var myEmail: String? = null
    private var myPassword: String? = null
    private var myName: String? = null
    private var myPhoneNum: String? = null
    private var myPetName: String? = null
    private var myPetAge: String? = null
    private var myPetSpecies: String? = null
    private var myPetWeight: String? = null
    private var myPetCharacter: String? = null
    private val PASSWORD_MIN_LENGTH = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        binding.signInClose.setOnClickListener(this)
        binding.signInSignIn.setOnClickListener(this)
        binding.signInMyEmail.addTextChangedListener(this)
        binding.signInMyPassword.addTextChangedListener(this)
        binding.signInMyName.addTextChangedListener(this)
        binding.signInMyPhoneNum.addTextChangedListener(this)
        binding.signInMyPetName.addTextChangedListener(this)
        binding.signInMyPetAge.addTextChangedListener(this)
        binding.signInMyPetSpecies.addTextChangedListener(this)
        binding.signInMyPetWeight.addTextChangedListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {

            // ??????
            R.id.signIn_close -> dismiss()

            // ??????
            R.id.signIn_signIn -> {
                myEmail = binding.signInMyEmail.text.toString()
                myPassword = binding.signInMyPassword.text.toString()

                createAccount(myEmail!!, myPassword!!)
            }
        }

    }

    private var wrongAccess = false
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        wrongAccess = (
                        // ?????????
                        binding.signInMyEmail.length() > 5
                        // ????????????
                        && binding.signInMyPassword.length() >= PASSWORD_MIN_LENGTH
                        // ?????? &&
                        && binding.signInMyName.text.isNotEmpty()
                        // ????????? ??????
                        && binding.signInMyPhoneNum.length() >= 11
                        //  myPet ?????? &&
                        && binding.signInMyPetName.text.isNotEmpty()
                        // myPet ??????
                        && binding.signInMyPetAge.text.isNotEmpty()
                        // myPet ?????? &&
                        && binding.signInMyPetSpecies.text.isNotEmpty()
                        // myPet ??????
                        && binding.signInMyPetWeight.text.isNotEmpty()
                )


        if( wrongAccess ) {
            println("true")
            binding.signInSignIn.isEnabled = true
        } else {
            println("false")
            binding.signInSignIn.isEnabled = false
        }
    }

    override fun afterTextChanged(p0: Editable?) {
    }

    // ?????? ??????
    private fun createAccount(email: String, password: String) {

        if( email.isNotEmpty() && password.isNotEmpty() ) {
            auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener {
                    if( it.isSuccessful ) {
                        // DB ??? ??????
                        saveInfoToDB()
                        toastMessage(R.string.completeToJoin)
                        dismiss()

                    } else {
                        try {
                            it.result
                        } catch (e: RuntimeExecutionException) {
                            toastMessage(R.string.DuplicateEmail)
                        } catch (e: Exception) {
                            toastMessage(R.string.fail)
                        }
                    }
                }
        }
    }

    // DB ??? ??????
    private fun saveInfoToDB() {
        myName = binding.signInMyName.text.toString()
        myPhoneNum = binding.signInMyPhoneNum.text.toString()
        myPetName  = binding.signInMyPetName.text.toString()
        myPetAge = binding.signInMyPetAge.text.toString()
        myPetSpecies = binding.signInMyPetSpecies.text.toString()
        myPetWeight = binding.signInMyPetWeight.text.toString()
        myPetCharacter = binding.signInMyPetCharacter.text.toString()

        val userInfoDTO = UserInfoDTO()
        userInfoDTO.uid             = auth?.uid
        userInfoDTO.userEmail       = auth?.currentUser?.email
        userInfoDTO.userPassword    = myPassword
        userInfoDTO.userName        = myName
        userInfoDTO.userPhoneNum    = myPhoneNum
        userInfoDTO.userPetName     = myPetName
        userInfoDTO.userPetAge      = myPetAge
        userInfoDTO.userPetSpecies  = myPetSpecies
        userInfoDTO.userPetWeight   = myPetWeight
        userInfoDTO.userPetCharacter= myPetCharacter

        db  ?.collection(UserInfoConstants.USER_INFO)
            ?.document(auth!!.currentUser!!.uid)
            ?.set(userInfoDTO)
            ?.addOnSuccessListener {
                println("??????")
            }
            ?.addOnFailureListener { e ->
                println("?????? >> ${e.message}")
            }

        // firebaseStorage ??? ?????? ????????? ????????? ?????? ??? ?????? ?????? ??????
        val profileImage = ProfileImage(mActivity, auth!!.currentUser!!.uid)
        profileImage.setBasicProfileImage(context)

    }

    private fun toastMessage(message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // ?????? ?????? ?????? ??? ????????? ?????????
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val focusView = currentFocus

        if( focusView != null)
            HideKeyboard().hideKeyboard(focusView, context, event)

        return super.dispatchTouchEvent(event)
    }


}