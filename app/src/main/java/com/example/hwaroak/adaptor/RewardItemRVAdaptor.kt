package com.example.hwaroak.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.hwaroak.R
import com.example.hwaroak.data.LockerItem

class RewardItemRVAdapter : RecyclerView.Adapter<RewardItemRVAdapter.ViewHolder>() {
    private var items = listOf<LockerItem?>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.iv_reward_item_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_reward_item, parent, false)
        return ViewHolder(view)
    }

    var onItemClick: ((LockerItem) -> Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position]?.let { item ->
            holder.imageView.setImageResource(item.imageRes)
            holder.itemView.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun setData(newItems: List<LockerItem?>) {
        items = newItems
        notifyDataSetChanged()
    }
}