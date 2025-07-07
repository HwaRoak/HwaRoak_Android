package com.example.hwaroak.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.hwaroak.R
import com.example.hwaroak.data.DiaryEmotion

class DiaryEmotionAdaptor(
    private val items: List<DiaryEmotion>,
    private val selecteditems: MutableSet<DiaryEmotion>
) : RecyclerView.Adapter<DiaryEmotionAdaptor.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val emotionImage: ImageView = itemView.findViewById(R.id.diary_recycler_imv)
        val emotionText: TextView = itemView.findViewById(R.id.diary_recycler_tv)

        val emotionLayout : ConstraintLayout = itemView.findViewById(R.id.diary_recycler_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diary_emotion, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.emotionImage.setImageResource(item.image)
        holder.emotionText.text = item.name

        //선택된 것에 포함되었는지 비교해서 색깔 check
        val bgColor = ContextCompat.getColor(holder.emotionLayout.context,
            if(selecteditems.contains(item)) R.color.colorGrayIcon else R.color.colorBackground)

        holder.emotionLayout.setBackgroundColor(bgColor)

        //아이템 눌렀을 때 처리 (최대 3개)
        holder.emotionLayout.setOnClickListener {
            if(selecteditems.contains(item)){
                selecteditems.remove(item)
            }
            else{
                if(selecteditems.size < 3) {
                    selecteditems.add(item)
                }
            }
            notifyItemChanged(position)

        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

}