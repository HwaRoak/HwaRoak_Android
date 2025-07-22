package com.example.hwaroak.ui.calendar

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.hwaroak.R
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.diary.access.CalendarViewModel
import com.example.hwaroak.api.diary.access.CalendarViewModelFactory
import com.example.hwaroak.api.diary.access.DiaryViewModelFactory
import com.example.hwaroak.api.diary.model.DiaryMonthResponse
import com.example.hwaroak.api.diary.repository.DiaryRepository
import com.example.hwaroak.data.DiaryContent
import com.example.hwaroak.data.DiaryEmotion
import com.example.hwaroak.databinding.FragmentCalendarBinding
import com.example.hwaroak.ui.main.MainActivity
import com.google.android.material.button.MaterialButton
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale
import kotlin.collections.mutableSetOf
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


class CalendarFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentCalendarBinding

    //일단 임시 data (선택한 날짜 및 정보)
    private lateinit var selectedDate: CalendarDay
    private lateinit var todayDate : LocalDate
 /*
    private var tmpData: MutableMap<CalendarDay, EmotionResult> =
        mutableMapOf(
            CalendarDay.today() to EmotionResult(
            conversation = "오늘도 참 재미있는 일이 있었네!",
            emotion = mutableSetOf<DiaryEmotion>(
                DiaryEmotion("행복한", R.drawable.ic_emotion3)
            )
            ),
            CalendarDay.from(2025, 7, 7) to EmotionResult(
            conversation = "오늘은 그저 그랬구나!",
            emotion = mutableSetOf<DiaryEmotion>(
                DiaryEmotion("무료함", R.drawable.ic_emotion10),
                DiaryEmotion("피곤함", R.drawable.ic_emotion11),
                DiaryEmotion("스트레스", R.drawable.ic_emotion15)
            )
            ),
            CalendarDay.from(2025, 7, 6) to EmotionResult(
                conversation = "오늘은 집에 가고 싶었구나!",
                emotion = mutableSetOf<DiaryEmotion>(
                    DiaryEmotion("스트레스", R.drawable.ic_emotion15)
                )
            ),
            CalendarDay.from(2025, 6, 6) to EmotionResult(
            conversation = "오늘은 집에 가고 싶었구나!",
            emotion = mutableSetOf<DiaryEmotion>(
                DiaryEmotion("피곤함", R.drawable.ic_emotion11)
            )
        )
    )
*/

    //캘린터 viewModel 정의
    private val diaryRepository by lazy {
        DiaryRepository(HwaRoakClient.diaryService)
    }
    private val calendarViewModel: CalendarViewModel by viewModels(
        factoryProducer = { CalendarViewModelFactory(diaryRepository)}
    )

    //token 받기
    private lateinit var pref: SharedPreferences
    private lateinit var accessToken: String

    //API로 받은 것을 바탕으로 설정한 map
    private val diaryMap = mutableMapOf<CalendarDay, DiaryMonthResponse>()


    //각 달력의 데코레이터
    private lateinit var todayDec: TodayDecorator
    private lateinit var selectedDec: SelectedDecorator
    private lateinit var sundayDec: SundayDecorator
    private lateinit var monthDec: MonthDecorator


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
        pref = requireContext().getSharedPreferences("user", MODE_PRIVATE)
        accessToken = pref.getString("accessToken", "").toString()

        //초기화
        todayDec    = TodayDecorator(requireContext())
        selectedDec = SelectedDecorator(requireContext())
        sundayDec   = SundayDecorator(requireContext())
        /**이 부분은 최초에 AP에서 값들들 가져왔을 때의 경우 -> 처음은 emptyset observer에서 휘치**/
        monthDec = MonthDecorator(requireContext(), emptySet())

        //0. 옵저버 설정 및 위의 값 설정
        observeMonthData()

        //1. 달력 초기화 및 리스너 달기
        initCalendar()

        //2. 상세보기(수정하기 페이지 이동)
        goEditFragment()

        //삭제하기
        showDeleteDialog()




    }


    //옵저버 설정(딱 1번)
    //viewModel의 List<> -> Map<>로 바꿔서 저장
    private fun observeMonthData(){
        calendarViewModel.monthDiaryResult.observe(viewLifecycleOwner){result ->
            //resp = list<diaryMonthResponse>
            result.onSuccess { resp->
                //일단 초기화
                diaryMap.clear()
                //diary = diaryMontheResponse
                resp.forEach { diary->
                    val localDate = LocalDate.parse("2025-07-21")
                    val day = CalendarDay.from(localDate)
                    diaryMap[day] = diary
                }
                //각 달에 대한 정보 넣고 달력 refresh
                monthDec.updateDates(diaryMap.keys)
                binding.calendarCalendarView.invalidateDecorators()
                //변경 후 getDataFromData 따단
                if (diaryMap.containsKey(selectedDate)) {
                    getDataFromDate(selectedDate)
                }

            }.onFailure { error ->
                Toast.makeText(requireContext(), "달력 로드 실패: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    //상세 보기 페이지로 이동(여부 판단) --> 여기는 일기 상세 페이지 갖고 와서 돌리는 걸로
    private fun goEditFragment(){
        //일단 그날에 대한 일기 정보(날짜, 일기 내용, 감정)
/*
        binding.calendarGodetailBtn.setOnClickListener {
            //1. 번들에 담기 전에 일기 작성 기록이 있는지 체크
            val nowDiary = diaryMap[selectedDate] ?: DiaryMonthResponse(-1, emptyList(),
                "아직 일기를 기록하지 않았어요", 0, -1)


            val diaryContent = DiaryContent(
                date = selectedDate,
                content = "이게 일기의 내용이래요!!!",
                emotions = nowDiary.emotionList
            )
            val bundle = Bundle().apply {
                putParcelable("KEY_RESULT", diaryContent)
            }
            Log.d("log_diary", "CalendarFragment에서 오늘 선택한 날짜의 일기 정보 bundle에 넣어 보내기")
            //2. 이동
            (requireActivity() as MainActivity).navigateToDiaryWith(bundle)

        }
        */
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
            view.findViewById<MaterialButton>(R.id.dialog_cancel_btn).setOnClickListener {
                dialog.dismiss()
            }
            view.findViewById<MaterialButton>(R.id.dialog_delete_btn).setOnClickListener {
                //id가 유효한 경우에만 수행
                diaryMap[selectedDate]?.id?.let{
                    calendarViewModel.deleteDiary(accessToken, diaryMap[selectedDate]!!.id)
                    //낙관적 업데이트
                    diaryMap.remove(selectedDate)
                    monthDec.updateDates(diaryMap.keys)
                    //달력에 알림
                    binding.calendarCalendarView.invalidateDecorators()
                    //데이터 업데이트(상세페이지 업데이트)
                    getDataFromDate(selectedDate)
                }
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
            addDecorators(monthDec)

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
                    //4. 선택한 날짜 저장 및 상세 페이지 넣기
                    getDataFromDate(date)
                }
            }

            //이번에는 월이 바뀌었을 때 반응
            setOnMonthChangedListener { _, date ->
                //api 호출
                calendarViewModel.getMonthDiary(accessToken, date.year, date.month)
                
            }
        }
        
        //3. 오늘 날짜 get 및 상세 페이지에 넣기
        selectedDate = CalendarDay.today()
        calendarViewModel.getMonthDiary(accessToken, selectedDate.year, selectedDate.month)
        //getDataFromDate(selectedDate)

    }

    //세부 정보 가져오기 및 세팅(일단은 Map에서 가져오지만 나중에는 해당 날짜를 보내고 get)
    private fun getDataFromDate(date : CalendarDay){

        /**원래는 date를 파싱해서 API 주고 받는 거
         * 그러나 지금은 map에서 가져온다고 가정
         * 근데 이미 해당 월에 대한 데이터를 가져온다면??
         * **/

        val emotionIcon : MutableMap<String, Int> = mutableMapOf(
            "차분한" to R.drawable.ic_emotion1,
            "뿌듯한" to R.drawable.ic_emotion2,
            "행복한" to R.drawable.ic_emotion3,
            "기대됨" to R.drawable.ic_emotion4,
            "설렘" to R.drawable.ic_emotion5,
            "감사함" to R.drawable.ic_emotion6,
            "신나는" to R.drawable.ic_emotion7,
            "슬픈" to R.drawable.ic_emotion8,
            "화나는" to R.drawable.ic_emotion9,
            "무료함" to R.drawable.ic_emotion10,
            "피곤함" to R.drawable.ic_emotion11,
            "짜증남" to R.drawable.ic_emotion12,
            "외로움" to R.drawable.ic_emotion13,
            "우울함" to R.drawable.ic_emotion14,
            "스트레스" to R.drawable.ic_emotion15

        )

        //일기 기록이 없는 경우
        val entry = diaryMap[date] ?: DiaryMonthResponse(-1, emptyList(),
            "아직 일기를 기록하지 않았어요", 0, -1)
        selectedDate = date

        // 1) “2025-07-04” 형태
        val ISO_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        // 2) “23 수” 형태 (일 월 화 … 토 중 한 글자)
        val DAY_WEEK_FMT = DateTimeFormatter.ofPattern("d E", Locale.KOREAN)

        var dayFormat1 = date.date.format(ISO_FMT)
        var dayFormat2 = date.date.format(DAY_WEEK_FMT)
        Log.d("log_calendar", dayFormat1)
        Log.d("log_calendar", dayFormat2)

        //감정들 string만 뽑아서
        var emotionString = if(entry.emotionList.isEmpty()){"기록 없음"}
        else{entry.emotionList.joinToString(" ")}
        //화록의 한마디
        binding.calendarTodayHwaroakTalkTv.text = entry.feedback
        //오늘의 날짜
        binding.calendarTodayTv.text = dayFormat2
        //감정 출력
        binding.calendarFeelingTv.text = emotionString
        //감정 이모티콘
        if(entry.emotionList.size > 1){
            binding.calendarEmotionCharacterImv.setImageResource(R.drawable.ic_calendar_asura)
        }
        else{
            var tmpIconId : Int
            if(emotionString.equals("기록 없음")){tmpIconId = R.drawable.ic_calendar_mupyojeong}
            else{tmpIconId = emotionIcon[entry.emotionList.get(0)] ?: R.drawable.ic_calendar_mupyojeong}
            binding.calendarEmotionCharacterImv.setImageResource(tmpIconId)
        }
        //삭제 버튼 활성화 여부
        if(entry.emotionList.size > 0){
            binding.calendarDeleteBtn.isEnabled = true
            binding.calendarDeleteBtn.setBackgroundResource(R.drawable.bg_diary_write_btn)
        }
        else{
            binding.calendarDeleteBtn.isEnabled = false
            binding.calendarDeleteBtn.setBackgroundResource(R.drawable.bg_diary_write_no_btn)
        }

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