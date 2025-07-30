package com.example.hwaroak.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hwaroak.R
import com.example.hwaroak.data.AnnouncementData
import com.example.hwaroak.databinding.ItemAnnouncementBinding

class AnnouncementRVAdaptor(
    private val noticeList: List<AnnouncementData>
) : RecyclerView.Adapter<AnnouncementRVAdaptor.NoticeViewHolder>() {

    inner class NoticeViewHolder(val binding: ItemAnnouncementBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding = ItemAnnouncementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val notice = noticeList[position]

        with(holder.binding) {
            // 제목과 상세 내용 설정
            announcementTitle.text = notice.title
            val date = notice.createdAt.replace("T", " | ").substringBefore(".")

            val content = "${date} 작성\n\n${notice.detail}"

            announcementDetail.text = content

            // 펼침/접힘 상태에 따른 visibility와 화살표 아이콘 변경
            announcementDetail.visibility = if (notice.isExpanded) View.VISIBLE else View.GONE
            btnToggle.setImageResource(
                if (notice.isExpanded) R.drawable.ic_arrow_down else R.drawable.ic_arrow_right
            )

            // 클릭 시 isExpanded 상태 토글 및 아이템 업데이트
            containerArea.setOnClickListener {
                notice.isExpanded = !notice.isExpanded
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int = noticeList.size
}
