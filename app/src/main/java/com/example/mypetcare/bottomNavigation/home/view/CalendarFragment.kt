package com.example.mypetcare.bottomNavigation.home.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CalendarView
import com.example.mypetcare.R
import com.example.mypetcare.bottomNavigation.home.schedule.adapter.ScheduleListAdapter
import com.example.mypetcare.bottomNavigation.home.schedule.view.ApplyDialog
import com.example.mypetcare.bottomNavigation.home.schedule.view.ScheduleCheckDialog
import com.example.mypetcare.database.constant.ScheduleConstants
import com.example.mypetcare.database.dto.UserScheduleDTO
import com.example.mypetcare.databinding.FragmentCalendarBinding
import com.example.mypetcare.listener.OnApplyTimeListener
import com.example.mypetcare.listener.OnCheckedBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import java.util.*
import kotlin.collections.ArrayList

class CalendarFragment : Fragment(), View.OnClickListener, CalendarView.OnDateChangeListener {

    private var mBinding : FragmentCalendarBinding? = null
    private val binding get() = mBinding!!
    private var db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    private var scheduleAdapter: ScheduleListAdapter? = null
    private var selectedYear: Int? = null
    private var selectedMonth: Int? = null
    private var selectedDate: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = FragmentCalendarBinding.inflate(inflater, container, false)
        binding.calendarSelectedDate.text = requireActivity().getString(R.string.today)

        // listAdapter 설정
        initAdapter()


        binding.calendarCalendarView.setOnDateChangeListener(this)
        binding.calendarApplyButton.setOnClickListener(this)
        binding.calendarListView.onItemClickListener = itemClickListener

        return binding.root
    }

    // listAdapter 설정
    private fun initAdapter() {
        scheduleAdapter = ScheduleListAdapter()
        binding.calendarListView.adapter = scheduleAdapter
    }



    override fun onClick(view: View?) {
        when(view?.id) {

            // 신청하기
            R.id.calendar_applyButton -> {
                println("신청하기")
                val applyDate: String = binding.calendarSelectedDate.text.toString()
                val applyDialog = ApplyDialog(requireContext(), applyDate)

                // 선택한 유형
                applyDialog.setOnCheck(object : OnCheckedBox {
                    override fun setCheckedCategory(category: Int) {
//                        binding.calendarSelectedCategory.text = category
                    }
                })

                applyDialog.setOnApplyTime(object : OnApplyTimeListener {
                    override fun setOnStartTime(selectedHour: Int, selectedMinute: Int) {
//                        if( selectedMinute == 0 )
//                            binding.calendarStartTime.text = "${selectedHour}시부터"
//                        else
//                            binding.calendarStartTime.text = "${selectedHour}시 ${selectedMinute}분부터"

                    }

                    override fun setOnEndTime(selectedHour: Int, selectedMinute: Int) {
//                        if( selectedMinute == 0 )
//                            binding.calendarEndTime.text = "${selectedHour}시까지"
//                        else
//                            binding.calendarEndTime.text = "${selectedHour}시 ${selectedMinute}분까지"
                    }
                })

                applyDialog.show()
            }

        }
    }

    // 달력에서 선택한 날짜
    override fun onSelectedDayChange(view: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        val today: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val selectedDay = requireActivity().getString(R.string.yyyy_mm_dd, year, month+1, dayOfMonth)

        if( dayOfMonth == today )
            binding.calendarSelectedDate.text = requireActivity().getString(R.string.today)
        else
            binding.calendarSelectedDate.text = selectedDay

        selectedYear = year
        selectedMonth = month + 1
        selectedDate = dayOfMonth

        // 일정 가져오기
        scheduleAdapter?.getScheduleList(selectedYear!!, selectedMonth!!, selectedDate!!)
    }

    // 일정 항목 클릭
    private val itemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val date = calendar.get(Calendar.DAY_OF_MONTH)

        if( selectedYear == null )
            getScheduleList(year, month, date, position)
        else
            getScheduleList(selectedYear!!, selectedMonth!!, selectedDate!!, position)
    }

    // 일정 가져오기
    private fun getScheduleList(year: Int, month: Int, date: Int, position: Int) {

        db  .collection(ScheduleConstants.USER_SCHEDULE)
            .document(uid.toString())
            .collection(year.toString())
            .document(month.toString())
            .collection(date.toString())
            .get()
            .addOnSuccessListener { result ->
                val data        = result.toObjects<UserScheduleDTO>()
                val category    = data[position].selectedCategory
                val startTime   = data[position].startTime
                val endTime     = data[position].endTime
                val request        = data[position].request
                val managerUid  = data[position].managerUid
                val managerName = data[position].managerName

                val setData = ArrayList<UserScheduleDTO>()
                setData.add(
                    UserScheduleDTO(
                    uid,
                    category,
                    startTime,
                    endTime,
                    request,
                    managerUid,
                    managerName,
                    ""
                )
                )

                val scheduleCheckDialog = ScheduleCheckDialog(requireActivity(), setData, year, month, date)
                scheduleCheckDialog.show()
            }
    }

}