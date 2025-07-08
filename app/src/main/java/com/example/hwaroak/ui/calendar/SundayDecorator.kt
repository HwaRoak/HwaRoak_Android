package com.example.hwaroak.ui.calendar

import android.content.Context
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.example.hwaroak.R
import org.threeten.bp.DayOfWeek

//매주 일요일을 빨갛게 표시
class SundayDecorator(private val context: Context) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        // CalendarDay.date 는 org.threeten.bp.LocalDate
        return day.date.dayOfWeek == DayOfWeek.SUNDAY
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(context, R.color.red)
            )
        )
    }
}
