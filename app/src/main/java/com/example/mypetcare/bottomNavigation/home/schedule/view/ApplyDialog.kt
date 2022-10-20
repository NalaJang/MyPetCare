package com.example.mypetcare.bottomNavigation.home.schedule.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.CompoundButton
import android.widget.TimePicker
import com.example.mypetcare.HideKeyboard
import com.example.mypetcare.R
import com.example.mypetcare.database.constant.ScheduleConstants
import com.example.mypetcare.databinding.DialogApplyBinding
import com.example.mypetcare.database.dto.UserScheduleDTO
import com.example.mypetcare.listener.OnApplyTimeListener
import com.example.mypetcare.listener.OnCheckedBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ResourceType")
class ApplyDialog constructor(context: Context, selectedDate: String): Dialog(context, R.drawable.dialog_full_screen)
    , View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private var mBinding: DialogApplyBinding? = null
    private val binding get() = mBinding!!
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    private var checkBoxListener : OnCheckedBox? = null
    private var applyTimeListener: OnApplyTimeListener? = null

    private var applyDate: String = selectedDate
    private var selectedYear: String? = null
    private var selectedMonth: String? = null
    private var selectedDate: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogApplyBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // view 초기화
        initView()


        binding.applyClose.setOnClickListener(this)
        binding.applyComplete.setOnClickListener(this)
        binding.applyWalk.setOnCheckedChangeListener(this)
        binding.applyBath.setOnCheckedChangeListener(this)
        binding.applyVisit.setOnCheckedChangeListener(this)
        binding.applyStartTime.setOnClickListener(this)
        binding.applyEndTime.setOnClickListener(this)

    }


    override fun onClick(view: View?) {
        when(view?.id) {
            // 닫기
            R.id.apply_close -> dismiss()

            // 시작 시간
            R.id.apply_startTime -> geApplyTime(R.id.apply_startTime)

            // 종료 시간
            R.id.apply_endTime -> geApplyTime(R.id.apply_endTime)

            // 완료
            R.id.apply_complete -> applyForSchedule()

        }
    }

    // view 초기화
    private fun initView() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        binding.applyDate.text = applyDate

        if( applyDate == context.getString(R.string.today) ) {
            applyDate = context.getString(R.string.yyyy_mm_dd, year, month+1, dayOfMonth)
        }

        val applyDateSplit = applyDate.split("년", "월")
        val dateSplit = applyDateSplit[2].substring(0, applyDateSplit[2].length -1)

        selectedYear = applyDateSplit[0].trim()
        selectedMonth = applyDateSplit[1].trim()
        selectedDate = dateSplit.trim()


        binding.applyStartTime.text = context.getString(R.string.startTime, hour)
        binding.applyEndTime.text = context.getString(R.string.endTime, hour+1)

        applyTimeListener?.setOnStartTime(hour, 0)
        applyTimeListener?.setOnEndTime(hour+1, 0)
    }


    // 선택한 신청 유형
    override fun onCheckedChanged(button: CompoundButton?, isChecked: Boolean) {
        if( isChecked ) {
            when(button?.id) {
                R.id.apply_walk -> checkBoxListener?.setCheckedCategory(R.string.walk)
                R.id.apply_bath -> checkBoxListener?.setCheckedCategory(R.string.bath)
                R.id.apply_visit -> checkBoxListener?.setCheckedCategory(R.string.visit)
            }
       }
    }


    // 신청 시간
    private fun geApplyTime(id: Int) {
        val calendar = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener{
            _: TimePicker?, hour: Int, minute: Int ->

            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            val startTime: String?
            val endTime: String?

            if( id == R.id.apply_startTime ) {
                if( minute == 0 ) {
                    startTime = context.getString(R.string.startTime, hour)
                } else
                    startTime = context.getString(R.string.selectedStartTime, hour, minute)

                binding.applyStartTime.text = startTime
                applyTimeListener?.setOnStartTime(hour, minute)

            } else if( id == R.id.apply_endTime ) {
                if( minute == 0 ) {
                    endTime = context.getString(R.string.endTime, hour)
                } else
                    endTime = context.getString(R.string.selectedEndTime, hour, minute)

                binding.applyEndTime.text = endTime
                applyTimeListener?.setOnEndTime(hour, minute)
            }
        }

        TimePickerDialog(context, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), 0, false).show()
    }


    fun setOnCheck(checkListener: OnCheckedBox) {
        checkBoxListener = checkListener
    }

    fun setOnApplyTime(listener: OnApplyTimeListener) {
        applyTimeListener = listener
    }

    // 일정 신청
    private fun applyForSchedule() {
        var selectedCategory = ""
        if( binding.applyWalk.isChecked )
            selectedCategory += binding.applyWalk.text.toString() + ","
        if( binding.applyBath.isChecked )
            selectedCategory += binding.applyBath.text.toString() + ","
        if( binding.applyVisit.isChecked )
            selectedCategory += binding.applyVisit.text.toString() + ","

        if( selectedCategory.isNotEmpty() ) // selectedCategory.length > 0
            selectedCategory = selectedCategory.substring(0, selectedCategory.length -1)

        val startTime = binding.applyStartTime.text.toString()
        val endTime = binding.applyEndTime.text.toString()
        val request = binding.applyRequest.text.toString()

        val now : Long = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
        val registrationTime = dateFormat.format(now)


        val userScheduleDTO = UserScheduleDTO()
        userScheduleDTO.uid = uid
        userScheduleDTO.selectedCategory = selectedCategory
        userScheduleDTO.startTime = startTime
        userScheduleDTO.endTime = endTime
        userScheduleDTO.request = request
        userScheduleDTO.registrationTime = registrationTime


        db  .collection(ScheduleConstants.USER_SCHEDULE)
            .document(uid.toString())
            .collection(selectedYear.toString())
            .document(selectedMonth.toString())
            .collection(selectedDate.toString())
            .document()
            .set(userScheduleDTO)
            .addOnSuccessListener {

                println("일정 등록 성공")
                dismiss()
            }
            .addOnFailureListener { e ->
                println("일정 등록 실패 >> ${e.message}")
            }

    }

    // 화면 바깥 터치 시 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView = currentFocus

        if( focusView != null)
            HideKeyboard().hideKeyboard(focusView, context, ev)

        return super.dispatchTouchEvent(ev)
    }
}

