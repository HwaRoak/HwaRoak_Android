package com.example.hwaroak.ui.calendar

import android.icu.util.Calendar
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.hwaroak.R
import com.example.hwaroak.data.DiaryContent
import com.example.hwaroak.data.DiaryEmotion
import com.example.hwaroak.databinding.FragmentCalendarBinding
import com.example.hwaroak.ui.main.MainActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.E

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CalendarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

//임시 클래스
data class EmotionResult(
    var conversation: String,
    var emotion: String,
)


class CalendarFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentCalendarBinding

    //일단 임시 data (선택한 날짜 및 정보)
    private lateinit var selectedDate: CalendarDay
    private lateinit var todayDate : LocalDate
    private var tmpData: MutableMap<CalendarDay, EmotionResult> =
        mutableMapOf(
            CalendarDay.today() to EmotionResult(
            conversation = "오늘도 참 재미있는 일이 있었네!",
            emotion = "행복함"
            ),
            CalendarDay.from(2025, 7, 7) to EmotionResult(
            conversation = "오늘은 그저 그랬구나!",
            emotion = "복합적"
            ),
            CalendarDay.from(2025, 7, 6) to EmotionResult(
                conversation = "오늘은 집에 가고 싶었구나!",
                emotion = "복합적"
            )
    )
    private var selectedEmotions: MutableSet<DiaryEmotion> =
        mutableSetOf(
            DiaryEmotion("차분한", R.drawable.ic_emotion1),
            DiaryEmotion("뿌듯한", R.drawable.ic_emotion2),
            DiaryEmotion("행복한", R.drawable.ic_emotion3)
        )



    //각 달력의 데코레이터
    private lateinit var todayDec: TodayDecorator
    private lateinit var selectedDec: SelectedDecorator
    private lateinit var sundayDec: SundayDecorator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //초기화
        todayDec    = TodayDecorator(requireContext())
        selectedDec = SelectedDecorator(requireContext())
        sundayDec   = SundayDecorator(requireContext())

        //달력 초기화 및 리스너 달기
        initCalendar()

        //상세보기(수정하기 페이지 이동)
        goEditFragment()

        //삭제하기
        showDeleteDialog()




    }

    //상세 보기 페이지로 이동
    private fun goEditFragment(){
        //일단 그날에 대한 일기 정보(날짜, 일기 내용, 감정)

        binding.calendarGodetailBtn.setOnClickListener {
            //1. 번들에 담기
            val emotionsList = ArrayList<DiaryEmotion>(selectedEmotions)
            Log.d("log_diary", "SEND: " + selectedDate.toString())
            val diaryContent = DiaryContent(
                date = selectedDate,
                content = "끼앗호우",
                emotions = emotionsList
            )
            val bundle = Bundle().apply {
                putParcelable("KEY_RESULT", diaryContent)
            }
            Log.d("log_diary", "보내기")
            //2. 이동
            (requireActivity() as MainActivity).navigateToDiaryWith(bundle)
        }
    }

    //삭제 다이얼로그 띄우기
    private fun showDeleteDialog(){

        binding.calendarDeleteBtn.setOnClickListener {
            //1. view 생성
            val view = LayoutInflater.from(requireContext()).
            inflate(R.layout.dialog_custom_delete_check, null, false)

            //2. 각 view의 요소 세팅 (다이얼로그에 보여줄 textView만 설정)
            view.findViewById<TextView>(R.id.dialog_main_tv).text = "기록을 삭제하시겠습니까?"

            //3. 다이얼로그 생성
            val dialog = androidx.appcompat.app.AlertDialog
                .Builder(requireContext(), R.style.CustomAlertDialog)
                .setView(view)
                .create()

            //4. 각 버튼의 리스너 달기
            view.findViewById<ImageButton>(R.id.dialog_cancel_btn).setOnClickListener {
                dialog.dismiss()
            }
            view.findViewById<ImageButton>(R.id.dialog_delete_btn).setOnClickListener {
                tmpData.remove(selectedDate)
                getDataFromDate(selectedDate)
                dialog.dismiss()

            }

            //5. 보여주기
            dialog.show()
        }
    }


    //달력 상태를 초기화하기
    private fun initCalendar(){

        //1. 달력 초기화
        val cal = binding.calendarCalendarView

        //일요일 시작(오늘 이후 터치 불가)
        cal.state().edit()
            .setFirstDayOfWeek(DayOfWeek.SUNDAY)
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        //오늘 날짜 표시 및 오늘 날짜 저장(막기 용도)
        cal.setCurrentDate(CalendarDay.today())
        todayDate = LocalDate.now()

        //달 표시
        val headerFmt = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
        cal.setTitleFormatter { day: CalendarDay ->
            day.date.format(headerFmt)
        }

        //주 표시
        cal.setWeekDayFormatter { dow: DayOfWeek ->
            val labels = listOf("S","M","T","W","T","F","S")
            // DayOfWeek.value: MONDAY=1…SUNDAY=7 → 7%7=0 으로 Sunday가 맨 앞
            val idx = dow.value % 7
            val txt = labels[idx]
            if (dow == DayOfWeek.SUNDAY) {
                //spannable로 텍스트 일부에 접근해 변경 가능
                SpannableString(txt).apply {
                    setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(requireContext(), R.color.red)
                        ), 0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            } else txt
        }

        //2. 데코레이터 적용 및 날짜 선택 리스너 설정
        cal.apply {
            // 한 번만 add!
            addDecorators(todayDec, selectedDec, sundayDec)

            //특정 날짜를 눌렀을 때 반응
            setOnDateChangedListener { _, date, selected ->
                if (selected) {
                    //0. 오늘 이후의 날짜면 처리 X
                    if(date.date.isAfter(todayDate)){
                        return@setOnDateChangedListener
                    }
                    //1. 직접 보관한 selectedDec 에 선택 날짜를 넘기고
                    selectedDec.setSelected(date)
                    //2. 달력 리프레시
                    invalidateDecorators()
                    //3. 로그 출력
                    Log.d("log_calendar", date.toString())
                    Log.d("log_diary", "TOUCH: " + date.toString())
                    //4. 선택한 날짜 저장 및 상세 페이지 넣기
                    getDataFromDate(selectedDate!!)
                }
            }
        }
        
        //3. 오늘 날짜 get 및 상세 페이지에 넣기
        selectedDate = CalendarDay.today()
        getDataFromDate(selectedDate)

    }

    //세부 정보 가져오기 및 세팅(일단은 Map에서 가져오지만 나중에는 해당 날짜를 보내고 get)
    private fun getDataFromDate(date : CalendarDay){

        /**원래는 date를 파싱해서 API 주고 받는 거
         * 그러나 지금은 map에서 가져온다고 가정
         * **/

        val entry = tmpData[date] ?: EmotionResult("", "")
        selectedDate = date

        // 1) “2025-07-04” 형태
        val ISO_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        // 2) “23 수” 형태 (일 월 화 … 토 중 한 글자)
        val DAY_WEEK_FMT = DateTimeFormatter.ofPattern("d E", Locale.KOREAN)

        var dayFormat1 = date.date.format(ISO_FMT)
        var dayFormat2 = date.date.format(DAY_WEEK_FMT)
        Log.d("log_calendar", dayFormat1)
        Log.d("log_calendar", dayFormat2)

        binding.calendarTodayHwaroakTalkTv.text = entry.conversation
        binding.calendarTodayTv.text = dayFormat2
        binding.calendarFeelingTv.text = entry.emotion

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CalendarFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalendarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}