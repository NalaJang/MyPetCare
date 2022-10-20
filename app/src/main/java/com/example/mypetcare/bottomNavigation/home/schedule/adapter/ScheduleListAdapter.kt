package com.example.mypetcare.bottomNavigation.home.schedule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.mypetcare.R
import com.example.mypetcare.database.constant.ScheduleConstants
import com.example.mypetcare.database.dto.UserScheduleDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class ScheduleListAdapter: BaseAdapter() {

    private var db = FirebaseFirestore.getInstance()
    private var uid = FirebaseAuth.getInstance().currentUser?.uid
    private var scheduleList: ArrayList<UserScheduleDTO> = arrayListOf()

    init {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // 일정 목록
        getScheduleList(year, month, day)
    }

    override fun getCount(): Int {
        return scheduleList.size
    }

    override fun getItem(position: Int): Any {
        return scheduleList[position]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, converView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(parent?.context).inflate(R.layout.item_schedule_list, parent, false)

        val startTime = view.findViewById<TextView>(R.id.scheduleList_startTime)
        val endTime = view.findViewById<TextView>(R.id.scheduleList_endTime)
        val selectedCategory = view.findViewById<TextView>(R.id.scheduleList_selectedCategory)

        val schedule = scheduleList[position]
        startTime.text = schedule.startTime
        endTime.text = schedule.endTime
        selectedCategory.text = schedule.selectedCategory

        return view
    }

    private fun getTodaySchedule() {
        println("getTodaySchedule")
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        db  .collection(ScheduleConstants.USER_SCHEDULE)
            .document(uid.toString())
            .collection(year.toString())
            .document(month.toString())
            .collection(day.toString())
            .get()
            .addOnCompleteListener { task ->
                if( task.isSuccessful ) {

                    scheduleList.clear()

                    for( i in task.result!! ) {

                        val category  = i.data[ScheduleConstants.CATEGORY].toString()
                        val startTime = i.data[ScheduleConstants.START_TIME].toString()
                        val endTime   = i.data[ScheduleConstants.END_TIME].toString()
                        val request      = i.data[ScheduleConstants.REQUEST].toString()
                        val registrationTime = i.data[ScheduleConstants.REGISTRATION_TIME].toString()

                        scheduleList.add(UserScheduleDTO(
                                            uid,
                                            category,
                                            startTime,
                                            endTime,
                                            request,
                                            registrationTime
                                        ))
                    }
                }

                notifyDataSetChanged()
            }
    }

    // 일정 목록
    fun getScheduleList(year: Int, month: Int, day: Int) {

        db  .collection(ScheduleConstants.USER_SCHEDULE)
            .document(uid.toString())
            .collection(year.toString())
            .document(month.toString())
            .collection(day.toString())
            .addSnapshotListener { snapshot, error ->

                scheduleList.clear()

                if( error != null )
                    return@addSnapshotListener

                if( snapshot != null ) {

                    for( document in snapshot ) {
                        val category         = document.getString(ScheduleConstants.CATEGORY).toString()
                        val startTime        = document.getString(ScheduleConstants.START_TIME).toString()
                        val endTime          = document.getString(ScheduleConstants.END_TIME).toString()
                        val request             = document.getString(ScheduleConstants.REQUEST).toString()
                        val managerUid       = document.getString(ScheduleConstants.MANAGER_UID).toString()
                        val managerName      = document.getString(ScheduleConstants.MANAGER_NAME).toString()
                        val registrationTime = document.getString(ScheduleConstants.REGISTRATION_TIME).toString()

                        scheduleList.add(UserScheduleDTO(
                                                        uid,
                                                        category,
                                                        startTime,
                                                        endTime,
                                                        request,
                                                        managerUid,
                                                        managerName,
                                                        registrationTime
                        ))

                    }
                    // list clear 후 새로 고침을 해준다.
                    notifyDataSetChanged()

                } else println("data null")
            }
    }

}