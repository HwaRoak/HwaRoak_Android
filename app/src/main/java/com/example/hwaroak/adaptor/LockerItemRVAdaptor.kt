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
            // 아이템이 있으면, 아이템 이미지 뷰에만 이미지를 설정
            holder.itemImage.setImageResource(item.imageRes)
            //각 아이템에 홈 화면 아이템변경을 위한 onclicklistener 설정
            holder.itemView.setOnClickListener {
                onShowConfirmDialog(item)
            }
        } else {
            // 아이템이 없으면, 아이템 이미지 뷰를 깨끗하게 비우기
            holder.itemImage.setImageResource(R.drawable.img_item_lock)
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