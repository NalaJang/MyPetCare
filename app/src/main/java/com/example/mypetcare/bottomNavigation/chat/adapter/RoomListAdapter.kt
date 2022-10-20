package com.example.mypetcare.bottomNavigation.chat.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mypetcare.database.constant.UserInfoConstants
import com.example.mypetcare.R
import com.example.mypetcare.bottomNavigation.chat.view.ChatActivity
import com.example.mypetcare.database.dto.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class RoomListAdapter: RecyclerView.Adapter<RoomListAdapter.ViewHolder>() {

    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private var databaseReference = FirebaseDatabase.getInstance().getReference(UserInfoConstants.CHAT_ROOM)
    private val roomList: ArrayList<ChatModel> = arrayListOf()
    private val roomInfo: ArrayList<ChatModel.Comment> = arrayListOf()
    private val roomUidList: ArrayList<String> = arrayListOf()
    private var roomUid: String? = null

    init {
        databaseReference.orderByChild("users/${uid}").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for( data in snapshot.children ) {

                        roomUid = data.key!!
                        roomUidList.add(roomUid!!)

                        // 채팅방 목록 가져오기
                        getRoomList()
                        // 채팅방 정보 가져오기
                        getRoomInfo()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_room_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        println("onBindViewHolder, ${roomList.size}")
        val comment: ChatModel.Comment = roomInfo[position]
        
        holder.roomName.text = comment.managerName
        holder.lastMessage.text = comment.message
        holder.latTime.text = comment.time

        // 클릭한 채팅방으로 이동
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ChatActivity::class.java)
            intent.putExtra("roomUid", roomUidList[position])
            intent.putExtra("managerName", comment.managerName)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }

        holder.deleteButton.setOnClickListener {
            deleteRoom(roomUidList[position])
        }
    }

    override fun getItemCount(): Int {
        return roomList.size
    }


    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val roomName = itemView.findViewById<TextView>(R.id.roomListAdapter_roomName)
        val lastMessage = itemView.findViewById<TextView>(R.id.roomListAdapter_lastMessage)
        val latTime = itemView.findViewById<TextView>(R.id.roomListAdapter_lastMsgTime)
        val deleteButton = itemView.findViewById<Button>(R.id.roomListAdapter_delete)
    }

    // firebase 에서 채팅방 목록 가져오기
    private fun getRoomList() {
        println("getRoomList")
        databaseReference.child(roomUid.toString())
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
//                                roomList.clear()

                                val item = snapshot.getValue<ChatModel>()
                                roomList.add(item!!)

                                notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
    }

    // 채팅방 정보 가져오기
    private fun getRoomInfo() {
        databaseReference.child(roomUid.toString())
            .child(UserInfoConstants.CHAT_COMMENTS)
            .limitToLast(1) // 마지막 메시지와 마지막 시간을 가져오기 위함
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                    roomInfo.clear()

                    val item = snapshot.getValue<ChatModel.Comment>()
                    roomInfo.add(item!!)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    println("getRoomInfo, onChildChanged")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    println("getRoomInfo, onChildRemoved")
                    println("${snapshot.key}")
                    roomList.clear()

                    val item = snapshot.getValue<ChatModel>()
                    roomList.add(item!!)
                    notifyDataSetChanged()
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }


            })
    }


    // 채팅방 삭제
    private fun deleteRoom(roomUid: String) {
        println("deleteRoom: ${roomUid}")
        databaseReference.child(roomUid).setValue(null)
    }
}