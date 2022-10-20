package com.example.mypetcare.bottomNavigation.home.managerInfo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mypetcare.database.constant.UserInfoConstants
import com.example.mypetcare.R
import com.example.mypetcare.database.dto.ReviewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class ReviewListAdapter(managerUid: String): RecyclerView.Adapter<ReviewListAdapter.ViewHolder>() {

    private val databaseReference = FirebaseDatabase.getInstance().getReference(UserInfoConstants.REVIEWS)
    private val userUid = FirebaseAuth.getInstance().currentUser?.uid
    private var reviewList = ArrayList<ReviewModel.Comment>()
    private val commentUidList = ArrayList<String>()
    private val reviewUidList = ArrayList<String>()
    private val mManagerUid = managerUid
    private var reviewUid: String? = null


    init {
        databaseReference.orderByChild("users/$mManagerUid").equalTo(true)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    println("onChildAdded 들어옴")
                    reviewUid = snapshot.key.toString()

                    // 리뷰 리스트 가져오기
                    getReviewList(reviewUid!!)
                    reviewUidList.add(reviewUid!!)

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    println("onChildChanged")
                    reviewList.clear()
                    commentUidList.clear()

                    /*
                     * 데이터 변화가 생기면 onChildChanged 에 들어오게 되는데
                     * 이 때의 snapshot.key 의 값은 변화가 있던 key 값만이 담기게 된다.
                     * 이 화면에서는 변화가 생긴 리뷰와 해당 manager 의 모든 리뷰를 가져와야 하기 때문에
                     * 처음에 불려지는 onChildAdded 에서 모든 reviewUid 를 저장한 reviewUidList 를 가져와서
                     * manager 의 리뷰 리스트를 갱신하도록 하였다.
                     */
                    for( uid in 0 until reviewUidList.size) {
                        getReviewList(reviewUidList[uid])
                        println("onChildChanged, reviewUidList[uid]: ${reviewUidList[uid]}")
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    println("onChildRemoved")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    println("onChildMoved")
                }

                override fun onCancelled(error: DatabaseError) {
                    println("onCancelled")
                }

            })

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewListAdapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_manager_review_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewListAdapter.ViewHolder, position: Int) {

        val item = reviewList[position]
        holder.setItem(item)

        // 로그인한 사람과 작성자 uid 비교 후 삭제 버튼 세팅
        if( userUid == item.uid ) {
            holder.deleteButton.visibility = View.VISIBLE
            holder.deleteButton.setOnClickListener{
                // 리뷰 삭제
                deleteReview(commentUidList[position])
            }
        } else
            holder.deleteButton.visibility = View.INVISIBLE
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val userName: TextView = view.findViewById(R.id.managerReview_userName)
        private val content: TextView = view.findViewById(R.id.managerReview_content)
        private val writingTime: TextView = view.findViewById(R.id.managerReview_writingTime)
        val deleteButton: Button = view.findViewById(R.id.managerReview_delete)

        fun setItem(item: ReviewModel.Comment) {
            userName.text    = item.userName
            content.text     = item.content
            writingTime.text = item.writingTime
        }
    }

    // 리뷰 리스트 가져오기
    private fun getReviewList(reviewUid: String) {

        databaseReference.child(reviewUid).child(UserInfoConstants.REVIEW_COMMENT)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    for( data in snapshot.children ) {

                        val item        = data.getValue<ReviewModel.Comment>()
                        val writerUid   = item?.uid.toString()
                        val userName    = item?.userName.toString()
                        val managerName = item?.managerName.toString()
                        val writingTime = item?.writingTime.toString()
                        val content     = item?.content.toString()
                        val commentUid  = data.key.toString()

                        reviewList.add(ReviewModel.Comment(
                            writerUid, userName, managerName, writingTime, content
                        ))

                        commentUidList.add(commentUid)
                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


    // 리뷰 삭제
    private fun deleteReview(selectedReview: String) {
        databaseReference.orderByChild("users/$userUid").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for( item in snapshot.children ) {
                        val reviewModel = item.getValue<ReviewModel>()

                        if( reviewModel?.users!!.containsKey(mManagerUid) ) {

                            val commentKey = item.key.toString()

                            // 리뷰 value null 설정
                            setReviewValueNull(commentKey, selectedReview)
                        }
                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun setReviewValueNull(reviewUid: String, selectedReview: String) {
        databaseReference.child(reviewUid)
                         .child(UserInfoConstants.REVIEW_COMMENT)
                         .child(selectedReview)
                         .setValue(null)
    }
}