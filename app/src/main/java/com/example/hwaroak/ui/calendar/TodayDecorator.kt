package com.example.hwaroak.ui.calendar

import android.content.Context
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.example.hwaroak.R

//오늘 날짜에 대해 표시하기
class TodayDecorator(private val context: Context) : DayViewDecorator {
    private val today = CalendarDay.today()
    private val icon = ContextCompat.getDrawable(context, R.drawable.ic_calendar_today)!!

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == today
    }

    override fun decorate(view: DayViewFacade) {
        // 배경 아이콘
        view.setBackgroundDrawable(icon)
        // 날짜 숫자 흰색
        view.addSpan(ForegroundColorSpan(Color.WHITE))
    }
}
