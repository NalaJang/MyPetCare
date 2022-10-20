package com.example.mypetcare.bottomNavigation.setting.view

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.example.mypetcare.database.constant.UserInfoConstants
import com.example.mypetcare.HideKeyboard
import com.example.mypetcare.R
import com.example.mypetcare.database.Cache
import com.example.mypetcare.database.firebase.ProfileImage
import com.example.mypetcare.database.firebase.GetUserInfo
import com.example.mypetcare.databinding.DialogMyProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyProfile : DialogFragment(), View.OnClickListener, TextWatcher {

    private var mBinding: DialogMyProfileBinding? = null
    private val binding get() = mBinding!!
    private var db = FirebaseFirestore.getInstance()
    private var uid = FirebaseAuth.getInstance().currentUser?.uid

    private val fileName = "${uid}.png"
    private var cache: Cache? = null
    private var profileImageUri: Uri? = null
    private var profileImage: ProfileImage? = null
    private lateinit var getResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_fullscreen)
        isCancelable = false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogMyProfileBinding.inflate(inflater, container, false)

        val getUserInfo = GetUserInfo()
        cache = Cache(requireActivity(), fileName)

        profileImage = ProfileImage(requireActivity(), uid!!)

        // 사용자 정보 가져오기
        getUserInfo.getUserInfo(binding.profileMyName,
                                binding.profileMyPhoneNum,
                                binding.profileMyPetName,
                                binding.profileMyPetAge,
                                binding.profileMyPetWeight,
                                binding.profileMyPetSpecies,
                                binding.profileMyPetCharacter,
                                requireContext()
                            )
        // 사용자 프로필 이미지 가져오기
        try {
            // 캐시에서 bitmap 이미지 가져오기
            cache!!.getImageFromCache(binding.profileProfileImage)

        } catch (e: java.lang.Exception) {
            println("프로필 이미지 e: ${e.message}")
            profileImage!!.getProfileImage(binding.profileProfileImage)
        }

        // 프로필 이미지 변경에 대한 결과값 받기
        getActivityResult()

        binding.proFileClose.setOnClickListener(this)
        binding.profileProfileImage.setOnClickListener(this)
        binding.profileDeleteProfileImage.setOnClickListener(this)
        binding.profileMyPetName.addTextChangedListener(this)
        binding.profileMyPetAge.addTextChangedListener(this)
        binding.profileMyPetSpecies.addTextChangedListener(this)
        binding.profileMyPetWeight.addTextChangedListener(this)
        binding.profileEdit.setOnClickListener(this)
        binding.profileLayout.setOnTouchListener { _, _ ->
            // 키보드 바깥 터치 시 키보드 내리기
            HideKeyboard().hideKeyboardInDialogFragment(requireContext(), requireView())
            true
        }

        return binding.root
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            // 닫기
            R.id.proFile_close -> dismiss()

            // 프로필 이미지 변경
            R.id.profile_profileImage -> openGallery()

            // 프로필 이미지 삭제 -> 기본 이미지로 저장
            R.id.profile_deleteProfileImage -> {
                profileImageUri = null
                // imageView 에 기본 이미지 설정
                binding.profileProfileImage.setImageResource(R.drawable.basic_profile_image)
            }

            // 수정
            R.id.profile_edit -> {

                // 정보 업데이트
                updateInfo()

                // firebaseStorage 에 이미지 업로드
                if( profileImageUri == null )
                    profileImage!!.setBasicProfileImage(requireContext())
                else
                    profileImage!!.uploadProfileImageToFirebase(profileImageUri!!)

                // firebaseStorage 에서 이전 이미지 삭제
                profileImage!!.deleteProfileImageFromFirebase()
            }
        }
    }


    // 프로필 이미지 변경에 대한 결과값 받기
    private fun getActivityResult() {
        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if( result.resultCode == RESULT_OK && result.data != null ) {
                profileImageUri = result.data?.data

                // 선택한 이미지 bitmap 변환 후 cache 파일로 저장
                try {
                    val inputStream = requireActivity().contentResolver.openInputStream(profileImageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream!!.close()

                    // non-null 체크
                    bitmap?.let {
                        binding.profileProfileImage.setImageBitmap(bitmap)
                        // 내부 저장소에 파일 저장
                        cache!!.saveImageToCache(bitmap)

                    } ?: let {
                        // bitmap 이 null 일 경우
                        binding.profileProfileImage.setImageURI(profileImageUri)
                    }
                } catch (e: Exception) { }
            }
        }
    }

    // 휴대폰 갤러리 접근
    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        getResult.launch(intent)
    }

    // 정보 업데이트
    private fun updateInfo() {
        val map = mutableMapOf<String, Any>()
        map[UserInfoConstants.PET_NAME]      = binding.profileMyPetName.text.toString()
        map[UserInfoConstants.PET_AGE]       = binding.profileMyPetAge.text.toString()
        map[UserInfoConstants.PET_SPECIES]   = binding.profileMyPetSpecies.text.toString()
        map[UserInfoConstants.PET_WEIGHT]    = binding.profileMyPetWeight.text.toString()
        map[UserInfoConstants.PET_CHARACTER] = binding.profileMyPetCharacter.text.toString()

        db  .collection(UserInfoConstants.USER_INFO)
            .document(uid!!)
            .update(map)
            .addOnCompleteListener { task ->
                if( task.isSuccessful ) {
                    Toast.makeText(context, getString(R.string.completeToEdit), Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun getInstance(): MyProfile {
        return MyProfile()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if( binding.profileMyPetName.text.isEmpty() )
            binding.profileEdit.isEnabled = false

        if( binding.profileMyPetAge.text.isEmpty() )
            binding.profileEdit.isEnabled = false

        if( binding.profileMyPetSpecies.text.isEmpty() )
            binding.profileEdit.isEnabled = false

        if( binding.profileMyPetWeight.text.isEmpty() )
            binding.profileEdit.isEnabled = false
    }

    override fun afterTextChanged(s: Editable?) {
        if( binding.profileMyPetName.text.isNotEmpty() && binding.profileMyPetAge.text.isNotEmpty()
            && binding.profileMyPetSpecies.text.isNotEmpty() && binding.profileMyPetWeight.text.isNotEmpty() ) {
            binding.profileEdit.isEnabled = true
        }
    }

}