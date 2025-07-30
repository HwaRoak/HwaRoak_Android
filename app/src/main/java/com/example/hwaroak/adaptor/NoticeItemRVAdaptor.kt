package com.example.hwaroak.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hwaroak.data.NoticeItem
import com.example.hwaroak.databinding.ItemNoticeBinding

class NoticeItemRVAdaptor(private val noticeList: List<NoticeItem>) :
    RecyclerView.Adapter<NoticeItemRVAdaptor.NoticeViewHolder>() {

    inner class NoticeViewHolder(val binding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding = ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val notice = noticeList[position]

        holder.binding.tvNoticeTitle.text = notice.title
        holder.binding.tvNoticeContent.text = notice.content
        holder.binding.tvNoticeDay.text = notice.createdAt
    }

    override fun getItemCount(): Int = noticeList.size
}
