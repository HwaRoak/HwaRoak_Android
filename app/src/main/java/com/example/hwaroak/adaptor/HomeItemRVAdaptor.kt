package com.example.hwaroak.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.hwaroak.R
import com.example.hwaroak.data.LockerItem

class HomeItemRVAdapter : RecyclerView.Adapter<HomeItemRVAdapter.ViewHolder>() {
    private var items = listOf<LockerItem?>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.iv_item_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_locker_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position]?.let { holder.imageView.setImageResource(it.imageRes) }
    }

    override fun getItemCount(): Int = items.size

    fun setData(newItems: List<LockerItem?>) {
        items = newItems
        notifyDataSetChanged()
    }
}