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

        // ????????? ?????? ????????????
        getUserInfo.getUserInfo(binding.profileMyName,
                                binding.profileMyPhoneNum,
                                binding.profileMyPetName,
                                binding.profileMyPetAge,
                                binding.profileMyPetWeight,
                                binding.profileMyPetSpecies,
                                binding.profileMyPetCharacter,
                                requireContext()
                            )
        // ????????? ????????? ????????? ????????????
        try {
            // ???????????? bitmap ????????? ????????????
            cache!!.getImageFromCache(binding.profileProfileImage)

        } catch (e: java.lang.Exception) {
            println("????????? ????????? e: ${e.message}")
            profileImage!!.getProfileImage(binding.profileProfileImage)
        }

        // ????????? ????????? ????????? ?????? ????????? ??????
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
            // ????????? ?????? ?????? ??? ????????? ?????????
            HideKeyboard().hideKeyboardInDialogFragment(requireContext(), requireView())
            true
        }

        return binding.root
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            // ??????
            R.id.proFile_close -> dismiss()

            // ????????? ????????? ??????
            R.id.profile_profileImage -> openGallery()

            // ????????? ????????? ?????? -> ?????? ???????????? ??????
            R.id.profile_deleteProfileImage -> {
                profileImageUri = null
                // imageView ??? ?????? ????????? ??????
                binding.profileProfileImage.setImageResource(R.drawable.basic_profile_image)
            }

            // ??????
            R.id.profile_edit -> {

                // ?????? ????????????
                updateInfo()

                // firebaseStorage ??? ????????? ?????????
                if( profileImageUri == null )
                    profileImage!!.setBasicProfileImage(requireContext())
                else
                    profileImage!!.uploadProfileImageToFirebase(profileImageUri!!)

                // firebaseStorage ?????? ?????? ????????? ??????
                profileImage!!.deleteProfileImageFromFirebase()
            }
        }
    }


    // ????????? ????????? ????????? ?????? ????????? ??????
    private fun getActivityResult() {
        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if( result.resultCode == RESULT_OK && result.data != null ) {
                profileImageUri = result.data?.data

                // ????????? ????????? bitmap ?????? ??? cache ????????? ??????
                try {
                    val inputStream = requireActivity().contentResolver.openInputStream(profileImageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream!!.close()

                    // non-null ??????
                    bitmap?.let {
                        binding.profileProfileImage.setImageBitmap(bitmap)
                        // ?????? ???????????? ?????? ??????
                        cache!!.saveImageToCache(bitmap)

                    } ?: let {
                        // bitmap ??? null ??? ??????
                        binding.profileProfileImage.setImageURI(profileImageUri)
                    }
                } catch (e: Exception) { }
            }
        }
    }

    // ????????? ????????? ??????
    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        getResult.launch(intent)
    }

    // ?????? ????????????
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