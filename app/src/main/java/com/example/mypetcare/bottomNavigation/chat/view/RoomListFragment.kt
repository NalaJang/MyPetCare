package com.example.mypetcare.bottomNavigation.chat.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mypetcare.bottomNavigation.chat.adapter.RoomListAdapter
import com.example.mypetcare.databinding.FragmentRoomListBinding

class RoomListFragment : Fragment() {

    private var mBinding : FragmentRoomListBinding? = null
    private val binding get() = mBinding!!
    private lateinit var roomListAdapter: RoomListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentRoomListBinding.inflate(inflater, container, false)

        // adapter 설정
        initAdapter()


        return binding.root
    }

    // adapter 설정
    private fun initAdapter() {
        binding.roomListRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        roomListAdapter = RoomListAdapter()
        binding.roomListRecyclerView.adapter = roomListAdapter
    }

    override fun onResume() {
        super.onResume()
        // view 가 새로 그려졌을 때
        roomListAdapter.notifyDataSetChanged()
        println("roomListFra, onResume")
    }

}