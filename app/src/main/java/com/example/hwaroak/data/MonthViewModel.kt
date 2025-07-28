package com.example.hwaroak.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar

class MonthViewModel : ViewModel() {

    // 월 상태를 관리하는 내부 Calendar 객체
    private val calendar: Calendar = Calendar.getInstance()

    // UI에 전달할 최종 데이터를 관리하는 StateFlow
    private val _monthData = MutableStateFlow(generateDisplayState())
    val monthData: StateFlow<MonthDisplayState> = _monthData

    // -2: 저저번달, -1: 저번달, 0: 이번달(초기값)
    private var monthOffset = 0

    // 다음 달 버튼 클릭 시
    fun nextMonth() {
        if (monthOffset < 0) { // 0(이번달)이 최대치
            monthOffset++
            _monthData.value = generateDisplayState()
        }
    }

    // 이전 달 버튼 클릭 시
    fun previousMonth() {
        if (monthOffset > -2) { // -2(저저번달)가 최소치
            monthOffset--
            _monthData.value = generateDisplayState()
        }
    }

    // 현재 monthOffset을 기준으로 UI 상태 객체를 생성하는 함수
    private fun generateDisplayState(): MonthDisplayState {
        // 기준이 되는 현재 달 Calendar 객체 생성
        val baseCalendar = Calendar.getInstance()
        // offset 만큼 월을 이동시켜 현재 표시할 달을 계산
        baseCalendar.add(Calendar.MONTH, monthOffset)

        // 각 위치의 월 숫자 계산
        val currentDisplayMonth = baseCalendar.get(Calendar.MONTH) + 1

        val prevCalendar = baseCalendar.clone() as Calendar
        prevCalendar.add(Calendar.MONTH, -1)
        val previousDisplayMonth = prevCalendar.get(Calendar.MONTH) + 1

        val nextCalendar = baseCalendar.clone() as Calendar
        nextCalendar.add(Calendar.MONTH, 1)
        val nextDisplayMonth = nextCalendar.get(Calendar.MONTH) + 1

        // 버튼 활성화 로직
        val isNextEnabled = monthOffset < 0      // 이번달(0)보다 이전일 때만 활성화
        val isPreviousEnabled = monthOffset > -2 // 저저번달(-2)보다 이후일 때만 활성화

        return MonthDisplayState(
            previousMonthText = "${previousDisplayMonth}월달 보기",
            currentMonthText = "${currentDisplayMonth}월달에 작성된 일기",
            nextMonthText = "${nextDisplayMonth}월달 보기",
            isPreviousEnabled = isPreviousEnabled,
            isNextEnabled = isNextEnabled
        )
    }
}