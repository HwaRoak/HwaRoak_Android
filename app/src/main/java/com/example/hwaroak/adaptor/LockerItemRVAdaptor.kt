package com.example.hwaroak.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.hwaroak.R
import com.example.hwaroak.data.LockerItem

// 아이템이 없을 수도 있으므로 List<Item?> 타입으로 받기
class LockerItemRVAdaptor(
    private var itemList: List<LockerItem?>,
    private val onShowConfirmDialog: (LockerItem?) -> Unit
) : RecyclerView.Adapter<LockerItemRVAdaptor.ViewHolder>() {

    // 각 아이템 뷰의 구성요소를 보관하는 ViewHolder
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.iv_item_image)
    }

    // ViewHolder를 생성하고 레이아웃을 붙여주는 부분
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_locker_item, parent, false)
        return ViewHolder(view)
    }

    // 데이터와 ViewHolder를 결합하는 부분
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 현재 위치의 아이템을 가져오기. 아이템이 없으면 null
        val item = itemList[position]

        if (item != null) {
            // 아이템이 있는 경우:
            // 1. 이미지 설정
            holder.itemImage.setImageResource(item.imageRes)
            // 2. 패딩을 0으로 초기화 (뷰 재사용 시 자물쇠에 설정된 패딩이 남는 문제 방지)
            holder.itemImage.setPadding(0, 0, 0, 0)
            // 3. 클릭 리스너 설정
            holder.itemView.setOnClickListener {
                onShowConfirmDialog(item)
            }
        } else {
            // 아이템이 없으면 자물쇠 이미지 표시
            // 1. 자물쇠 이미지 설정
            holder.itemImage.setImageResource(R.drawable.img_item_lock)

            // 2. 자물쇠 이미지가 커보이지 않도록 패딩 추가
            val paddingInDp = 7 // 원하는 패딩 값(dp)
            val scale = holder.itemView.context.resources.displayMetrics.density
            val paddingInPx = (paddingInDp * scale + 0.5f).toInt()
            holder.itemImage.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)

            // 3. 클릭 이벤트 제거
            holder.itemView.setOnClickListener(null)
            holder.itemView.isClickable = false
        }
    }

    // 전체 아이템 개수를 반환
    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateData(newList: List<LockerItem?>) {
        itemList = newList
        notifyDataSetChanged()
    }
}