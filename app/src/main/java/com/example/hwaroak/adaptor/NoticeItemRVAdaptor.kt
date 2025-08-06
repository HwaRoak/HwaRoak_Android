package com.example.hwaroak.adaptor

import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hwaroak.R
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.notice.repository.NoticeRepository
import com.example.hwaroak.data.NoticeItem
import com.example.hwaroak.databinding.ItemNoticeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoticeItemRVAdaptor(private val noticeList: List<NoticeItem>,
                          private val accessToken: String,
                          private val onItemClick: (NoticeItem) -> Unit) :
    RecyclerView.Adapter<NoticeItemRVAdaptor.NoticeViewHolder>() {


    //읽기 관련 호출
    private val noticeRepository by lazy {
        NoticeRepository(HwaRoakClient.noticeService)
    }


    inner class NoticeViewHolder(val binding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding = ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val notice = noticeList[position]

        //기본 세팅

        holder.binding.tvNoticeTitle.text = notice.title
        holder.binding.tvNoticeContent.text = notice.content
        holder.binding.tvNoticeDay.text = notice.createdAt

        val context = holder.itemView.context
        val backgroundColor = if (notice.isRead) context.getColor(R.color.colorGrayIcon)
            else context.getColor(R.color.white)

        holder.binding.ivNoticeCardview.backgroundTintList = ColorStateList.valueOf(backgroundColor)

        //누를 때 변화
        holder.itemView.setOnClickListener {
            //1. 읽기 표시 - 일단 실행한 하면 그만
            if(!notice.isRead) {
                CoroutineScope(Dispatchers.Main).launch {
                    noticeRepository.readNotice(accessToken, notice.id)
                }
            }
            //2. 느슨하게 체크
            holder.binding.ivNoticeCardview.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.colorGrayIcon))

            //3. 콜백 함수에 터치한 거 넣기 이거로 판단
            onItemClick(notice)
        }

    }

    override fun getItemCount(): Int = noticeList.size
}
