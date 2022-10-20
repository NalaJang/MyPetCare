package com.example.mypetcare.login

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import com.example.mypetcare.HideKeyboard
import com.example.mypetcare.R
import com.example.mypetcare.bottomNavigation.BottomNavigation
import com.example.mypetcare.databinding.ActivityLoginBinding
import com.example.mypetcare.signUp.SignUpDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity(), TextWatcher, View.OnClickListener {

    private var mBinding : ActivityLoginBinding? = null
    private val binding get() = mBinding!!
    private lateinit var auth: FirebaseAuth
    private var db: FirebaseFirestore? = null
    private var uid: String? = null
    private val PASSWORD_MIN_LENGTH = 6


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        // "가입하기"에 밑줄
        binding.loginSignIn.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        binding.loginEmail.addTextChangedListener(this)
        binding.loginPassword.addTextChangedListener(this)
        binding.loginLogin.setOnClickListener(this)
        binding.loginSignIn.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {

            // 로그인
            R.id.login_login -> {

                // 입력된 정보 없이 로그인을 눌렀을 경우
                if( binding.loginEmail.length() < 1 )
                    binding.loginWarningEmail.visibility = View.VISIBLE

                if( binding.loginPassword.length() < 1 )
                    binding.loginWarningPassword.visibility = View.VISIBLE

                // 정보를 모두 입력했을 경우
                else if( binding.loginWarningEmail.isInvisible && binding.loginWarningPassword.isInvisible ) {
                    val myEmail = binding.loginEmail.text.toString()
                    val myPassword = binding.loginPassword.text.toString()

                    userLogin(myEmail, myPassword)

                }
            }

            // 회원가입
            R.id.login_signIn -> {
                val signInDialog = SignUpDialog(this)
                signInDialog.show()
            }
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }


    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if( binding.loginEmail.hasFocus() ) {
            if( binding.loginEmail.length() >= 3 )
                binding.loginWarningEmail.visibility = View.INVISIBLE
            else
                binding.loginWarningEmail.visibility = View.VISIBLE
        }

        if( binding.loginPassword.hasFocus() ) {
            if( binding.loginPassword.length() >= PASSWORD_MIN_LENGTH )
                binding.loginWarningPassword.visibility = View.INVISIBLE
            else
                binding.loginWarningPassword.visibility = View.VISIBLE
        }
    }

    override fun afterTextChanged(p0: Editable?) {
    }


    // 로그인
    private fun userLogin(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if( task.isSuccessful ) {

                    val intent = Intent(this, BottomNavigation::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    println("실패")
                }
            }
            .addOnFailureListener { e ->
                println("실패 >> ${e.message}")

                when(e.message) {
                    "The email address is badly formatted."
                        -> toastMessage("올바르지 않은 이메일 형식입니다.")

                    "The password is invalid or the user does not have a password."
                        -> toastMessage("아이디 또는 비밀번호가 맞지 않습니다.")

                    "An internal error has occurred"
                        -> toastMessage("인터넷 연결이 불안정합니다.")

                    "Ignoring header X-Firebase-Locale because its value was null."
                        -> toastMessage("인터넷 연결이 불안정합니다.")
                }
            }
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // 화면 바깥 터치 시 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView = currentFocus

        if( focusView != null)
            HideKeyboard().hideKeyboard(focusView, applicationContext, ev)

        return super.dispatchTouchEvent(ev)
    }

    override fun onDestroy() {
        super.onDestroy()

        mBinding = null
    }
}