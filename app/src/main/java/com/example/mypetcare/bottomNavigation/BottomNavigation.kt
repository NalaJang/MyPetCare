package com.example.mypetcare.bottomNavigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.mypetcare.R
import com.example.mypetcare.databinding.ActivityBottomNavigationBinding
import com.example.mypetcare.bottomNavigation.chat.view.RoomListFragment
import com.example.mypetcare.bottomNavigation.home.view.CalendarFragment
import com.example.mypetcare.bottomNavigation.setting.view.SettingFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.lang.IllegalArgumentException

class BottomNavigation : AppCompatActivity() {

    private var mBinding: ActivityBottomNavigationBinding? = null
    private val binding get() = mBinding!!
    private lateinit var auth: FirebaseAuth
    private var db: FirebaseFirestore? = null
    private var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        // 처음에 보여줄 fragment
        supportFragmentManager.beginTransaction().replace(R.id.bottom_container, CalendarFragment()).commit()

        // bottomNavigationView 세팅
       initNavigationView()

    }


    private fun initNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            replaceFragment(
                when(it.itemId) {
                    R.id.menu_calendar -> CalendarFragment()
                    R.id.menu_chat -> RoomListFragment()
                    R.id.menu_setting -> SettingFragment()
                    else -> throw IllegalArgumentException("not found menu item id.")
                }
            )
            true
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.bottom_container, fragment).commit()
    }

    override fun onDestroy() {
        super.onDestroy()

        mBinding = null

    }
}