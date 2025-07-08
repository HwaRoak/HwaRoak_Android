package com.example.hwaroak.ui.calendar

import android.content.Context
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.example.hwaroak.R

// 사용자가 터치한 날짜에 대해 표시
class SelectedDecorator(private val context: Context) : DayViewDecorator {
    private var selectedDay: CalendarDay? = null
    private val icon = ContextCompat.getDrawable(context, R.drawable.ic_calendar_selected)!!

    //내가 직접 선택한 날짜를 전달
    fun setSelected(day: CalendarDay) {
        selectedDay = day
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == selectedDay
    }

    override fun decorate(view: DayViewFacade) {
        view.setBackgroundDrawable(icon)

    }
}
