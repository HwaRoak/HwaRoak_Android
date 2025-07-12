package com.example.hwaroak.ui.calendar

import android.content.Context
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.example.hwaroak.R
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class MonthDecorator (private val context: Context,
    private var dates: Set<CalendarDay> = emptySet()) : DayViewDecorator {
        
        //일단 색깔과 크기
        private val dotColor = ContextCompat.getColor(context, R.color.colorPrimary)
    private val dotRadius = 6f

    /** 날짜 집합을 변경하고 싶을 때 호출 */
    fun updateDates(newDates: Set<CalendarDay>) {
        dates = newDates
    }

    //존재하는지 비교
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    //점 찍기
    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(dotRadius, dotColor))
    }


}