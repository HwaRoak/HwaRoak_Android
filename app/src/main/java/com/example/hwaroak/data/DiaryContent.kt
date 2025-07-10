package com.example.hwaroak.data

import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class DiaryContent(
    val date: CalendarDay,
    val content: String,
    val emotions: List<DiaryEmotion>
) : Parcelable
