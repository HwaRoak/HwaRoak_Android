package com.example.hwaroak.ui.friend

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hwaroak.R
import com.example.hwaroak.databinding.ItemFriendBinding
import com.google.android.material.button.MaterialButton

class FriendAdapter(private var friendList: MutableList<FriendData>, var isManageMode: Boolean = false,private val onAddFriendClicked: () -> Unit, private val onMarkForDelete: (FriendData) -> Unit,  private val onVisitClicked: (FriendData) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val VIEW_TYPE_FRIEND = 0 // 일반 친구
        const val VIEW_TYPE_ADD_BUTTON = 1 // 친구 추가 버튼
    }

    // 일반 친구 ViewHolder
    inner class FriendViewHolder(private val binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: FriendData) {

            //이미지
            Glide.with(itemView)
                .load(friend.profileImage)
                .placeholder(R.drawable.ic_friend_profile)
                .error(R.drawable.ic_friend_profile)
                .circleCrop()
                .into(binding.friendProfile)

            binding.friendTvName.text = friend.name
            binding.friendTvStatus.text = friend.status

            // 방문하기 버튼 클릭 → 콜백 전달
            binding.friendVisitTv.setOnClickListener {
                onVisitClicked(friend)
            }

            //방문하기 버튼은 일반 모드에서만 보여주기
            binding.friendVisitBtn.visibility = if (isManageMode) View.GONE else View.VISIBLE
            binding.friendVisitTv.visibility = if (isManageMode) View.GONE else View.VISIBLE

            // 마이너스 아이콘(삭제 버튼) 표시 여부
            binding.friendMinus.visibility =
                if (friend.isDeletable) View.VISIBLE else View.GONE
            // 삭제 아이콘 클릭 시 삭제 확인 다이얼로그 표시
            binding.friendMinus.setOnClickListener {
                showDeleteDialog(binding.root.context, adapterPosition)
            }
        }
    }

    // 삭제 다이얼로그 띄우기
    private fun showDeleteDialog(context: Context, position: Int) {
        // 1. 다이얼로그에 사용할 레이아웃 뷰 생성
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_delete_friend, null, false)

        // 2. 다이얼로그 내 텍스트 뷰 설정
        view.findViewById<TextView>(R.id.dialog_friend_tv).text = "친구를 삭제하시겠습니까?"

        // 3. AlertDialog 생성
        val dialog = AlertDialog.Builder(context, R.style.CustomAlertDialog)
            .setView(view)
            .create()

        //4. 취소 버튼 클릭 시 다이얼로그 종료
        view.findViewById<MaterialButton>(R.id.friend_cancel_btn).setOnClickListener {
            dialog.dismiss()
        }
        // 5. 삭제 버튼 클릭 시 해당 아이템 삭제 및 다이얼로그 종료
        view.findViewById<MaterialButton>(R.id.friend_delete_btn).setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                val friend = friendList[position]
                onMarkForDelete(friend) // ✅ 삭제 예정 리스트에 넣기 위해 콜백 호출

                friendList.removeAt(position) // UI에서만 제거
                notifyItemRemoved(position)

                dialog.dismiss()
            }
        }
        // 6. 다이얼로그 화면에 표시
        dialog.show()
    }

    // 친구 추가 버튼 ViewHolder
    inner class AddButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            val addButton = itemView.findViewById<MaterialButton>(R.id.friend_add_btn)
            addButton.setOnClickListener {
                onAddFriendClicked()
            }
        }
    }

    // 뷰 타입 결정
    override fun getItemViewType(position: Int): Int {
        return when {
            // isAddButton이 true면 찬구 추가 버튼
            friendList[position].isAddButton -> VIEW_TYPE_ADD_BUTTON
            else -> VIEW_TYPE_FRIEND
        }
    }

    //viewtype에 따라 viewHolder 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            //일반 친구 (추후 친구 화면 구현 필요)
            VIEW_TYPE_FRIEND -> {
                val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FriendViewHolder(binding)
            }
            //친구 추가 버튼
            VIEW_TYPE_ADD_BUTTON -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_add_friend, parent, false)
                AddButtonViewHolder(view)
            }
            // 예외처리
            else -> throw IllegalArgumentException("Unknown viewType $viewType")
        }
    }

    //ViewHolder에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            // 일반 친구
            is FriendViewHolder -> holder.bind(friendList[position])
            // 친구 추가 버튼
            is AddButtonViewHolder -> holder.bind() // 버튼 클릭 시 콜백
        }
    }

    //드래그 이동 기능 추가
    fun moveItem(fromPosition: Int, toPosition: Int) {
        val movedItem = friendList.removeAt(fromPosition)
        friendList.add(toPosition, movedItem)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun getItemCount(): Int = friendList.size

}