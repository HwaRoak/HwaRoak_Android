package com.example.hwaroak.ui.diary

import android.app.DatePickerDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.example.hwaroak.R
import com.example.hwaroak.adaptor.DiaryEmotionAdaptor
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.diary.access.DiaryViewModel
import com.example.hwaroak.api.diary.access.DiaryViewModelFactory
import com.example.hwaroak.api.diary.model.DiaryDetailResponse
import com.example.hwaroak.api.diary.model.DiaryResponseBody
import com.example.hwaroak.api.diary.model.DiaryWriteResponse
import com.example.hwaroak.api.diary.repository.DiaryRepository
import com.example.hwaroak.api.login.repository.LoginRepository
import com.example.hwaroak.data.DiaryContent
import com.example.hwaroak.data.DiaryEmotion
import com.example.hwaroak.databinding.FragmentDiaryWriteBinding
import com.example.hwaroak.ui.main.MainActivity
import org.json.JSONObject
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DiaryWriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiaryWriteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val emotionMap = mapOf(
        "차분한" to R.drawable.ic_emotion1,
        "뿌듯한" to R.drawable.ic_emotion2,
        "행복한" to R.drawable.ic_emotion3,
        "기대됨" to R.drawable.ic_emotion4,
        "설렘"   to R.drawable.ic_emotion5,
        "감사함" to R.drawable.ic_emotion6,
        "신나는" to R.drawable.ic_emotion7,
        "슬픈"   to R.drawable.ic_emotion8,
        "화나는" to R.drawable.ic_emotion9,
        "무료함" to R.drawable.ic_emotion10,
        "피곤함" to R.drawable.ic_emotion11,
        "짜증남" to R.drawable.ic_emotion12,
        "외로움" to R.drawable.ic_emotion13,
        "우울함" to R.drawable.ic_emotion14,
        "스트레스" to R.drawable.ic_emotion15
    )

    private lateinit var binding: FragmentDiaryWriteBinding

    //선택된 item view들의 집합
    private val selectedEmotions = mutableSetOf<DiaryEmotion>()

    //달력 개체
    private val calendar = Calendar.getInstance()

    //수정 모드인지 아니면 새로 쓰는지 확인
    private var isEditMode = false
    private lateinit var existDiary: DiaryDetailResponse

    //엑세스 토큰
    //유저 정보를 담을 sharedPreference
    private lateinit var pref: SharedPreferences
    private lateinit var accessToken: String

    //viewModel 정의
    // 1) Retrofit 서비스로 Repository 인스턴스 생성
    private val diaryRepository by lazy {
        DiaryRepository(HwaRoakClient.diaryService)
    }
    // 2) Activity 스코프로 ViewModel 생성 (Factory 주입)
    private val diaryViewModel: DiaryViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { DiaryViewModelFactory(diaryRepository) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //fragment 인자로 받았는지 확인
        arguments?.getParcelable<DiaryDetailResponse>("KEY_RESULT")?.let {
            isEditMode = true
            existDiary = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDiaryWriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireContext().getSharedPreferences("user", MODE_PRIVATE)
        accessToken = pref.getString("accessToken", "").toString()

        /**상단 바 표시 < **/
        (activity as? MainActivity)?.setTopBar("오늘의 화록",isBackVisible = true)

        //맨 처음 날짜 표시
        updateDateText()

        //만약 bundle로 가져올 경우
        //1. 일단 calender에서 온 것이니 날짜 최신화 체크
        //2. 만약 emotion이 1개 이상이면 기존 꺼 수정이니 수정 check
        /**
         * 로직이 꼬이는 문제가 발생
         * 달력에서 새로 입력하는 과정에서 UI문제 및 로드 문제가 발생
         * -> 이를 위해 달력에서 오더라도 새로 입력하는 경우는 강제로 분기를 일기쓰기 -> 새로 쓰기로 변경함
         * -> 감정이 없다 = 이전에 쓴 경험이 없다!
         * **/

        if(isEditMode){
            Log.d("log_diary", "Edit Mode IN")
            settingUIFromData()
            if(existDiary.emotionList.size>0){
                binding.diaryFinishOrEditTv.text = "수정하기"
                diaryViewModel.setEditMode() // isWrite = 2
                diaryViewModel.isEditCalendar = true
            }
            else{
                diaryViewModel.setWriteMode() // // isWrite = 1
                diaryViewModel.isEditCalendar = false
                isEditMode = false
            }

        }
        else{
            diaryViewModel.setWriteMode() // // isWrite = 1
            diaryViewModel.isEditCalendar = false
        }

        //recyclerviwe 연결 + 일기 작성 버튼 핸들링
        setRecyclerview()


        // 다이어리 작성 버튼 클릭 리스너 설정
        binding.diaryWriteDiarySwapBtn.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.diaryWriteDiaryContentCdv)
            val isVisible = binding.diaryWriteDiaryContentCdv.isVisible
            binding.diaryWriteDiaryContentCdv.visibility = if (isVisible) View.GONE else View.VISIBLE

            val angle = if (isVisible) -90f else 0f
            binding.diaryWriteDiarySwapBtn.animate()
                .rotation(angle)
                .setDuration(300)
                .start()

        }

        // 감정 표현 선택 버튼 클릭 리스너 설정
        binding.diaryEmotionswapBtn.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.diaryEmotionContentCdv)
            val isVisible = binding.diaryEmotionContentCdv.isVisible
            binding.diaryEmotionContentCdv.visibility = if (isVisible) View.GONE else View.VISIBLE

            val angle = if (isVisible) -90f else 0f
            binding.diaryEmotionswapBtn.animate()
                .rotation(angle)
                .setDuration(300)
                .start()

        }

        //작성 버튼 클릭 리스너 설정
        binding.diaryFinishBtn.setOnClickListener {
            //수정 모드 -> 근데 달력에서 온 걸 수도 있다!
            /**
             * 
             * 여기서 사실상 isEditMode = 달력에서 온 것 + 이전에 작성한 이력이 있는 것으로 판단
             * 
             * **/
            if(isEditMode){
                editDiaryWithAPI()
                
            }
            //새로 쓰기 모드
            else {
                if(diaryViewModel.isWrite.value == 1){
                    writeDiaryWithAPI()
                }
                else{
                    editDiaryWithAPI()
                }

            }


        }

        /**옵저버**/
        //1. 일기 수정
        diaryViewModel.editResult.observe(viewLifecycleOwner) { result ->
                binding.diaryLoadingProgressbar.visibility = View.GONE
                binding.diaryLoadingTv.visibility = View.GONE
                binding.diaryFinishBtn.setBackgroundResource(R.drawable.bg_diary_write_btn)
                binding.diaryFinishBtn.isEnabled = true
                result.onSuccess { resp ->
                    // 성공 시
                    Log.d("log_diary", "일기 수정 SUCCESS!")
                    Log.d("log_diary", "id=${resp.id} / response=${resp.feedback}")
                    Log.d("log_diary", "${resp.emotionList}")
                    diaryViewModel.lastDiaryId = resp.id

                    (parentFragment as DiaryFragment)
                        .binding
                        .diaryViewpager
                        .currentItem = 1

                }.onFailure { err ->
                    // 실패 시
                    Toast.makeText(
                        requireContext(),
                        "수정 중 오류 발생: {$err}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        //2. 일기 작성
        diaryViewModel.writeResult.observe(viewLifecycleOwner) { result ->
                binding.diaryLoadingProgressbar.visibility = View.GONE
                binding.diaryLoadingTv.visibility = View.GONE
                binding.diaryFinishBtn.setBackgroundResource(R.drawable.bg_diary_write_btn)
                binding.diaryFinishBtn.isEnabled = true
                result.onSuccess { resp ->
                    // 성공 시
                    Log.d("log_diary", "일기 작성 SUCCESS!")
                    Log.d("log_diary", "id=${resp.id} / response=${resp.feedback}")
                    Log.d("log_diary", "${resp.emotionList}")
                    diaryViewModel.lastDiaryId = resp.id

                    (parentFragment as DiaryFragment)
                        .binding
                        .diaryViewpager
                        .currentItem = 1
                }.onFailure { err ->
                    // 실패 시
                    Log.d("log_diary", err.message.toString())

                    //여기서 이미 일기를 작성한 에러인 경우 따로 처리
                    val raw = err.message ?: ""
                    // "HTTP 400: " 뒤의 순수 JSON 문자열만 추출
                    val jsonPart = raw.substringAfter(":").trim()
                    try {
                        val obj = JSONObject(jsonPart)
                        val code = obj.optString("code")
                        val message = obj.optString("message")
                        if (code == "DE4001" || code=="DBERROR4002") {
                            Toast.makeText(
                                requireContext(),
                                "오늘은 이미 일기를 작성하셨습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: Exception) {
                        // JSON 파싱 실패 시 기본 에러 토스트
                        Toast.makeText(
                            requireContext(),
                            "일기 저장 중 오류가 발생했습니다: ${err.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        //

    }

    //diaryEditAPI 호출
    private fun editDiaryWithAPI(){
        //일단 현재에서 수정해야 하는 경우
        binding.diaryFinishBtn.isEnabled = false
        binding.diaryFinishBtn.setBackgroundResource(R.drawable.bg_diary_write_no_btn)
        binding.diaryLoadingProgressbar.visibility = View.VISIBLE
        binding.diaryLoadingTv.visibility = View.VISIBLE
        if(diaryViewModel.isEditCalendar == false) {

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                val recordDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(calendar.time)
                val content: String = binding.diaryDiarycontentEdt.text.toString().trim()
                val emotionList: List<String> = selectedEmotions.map { it.name }
                val diaryId = diaryViewModel.lastDiaryId
                Log.d("log_diary","일기 수정 날짜: {$recordDate}")
                diaryViewModel.editDiary(accessToken, diaryId, recordDate, content, emotionList)

            }

        }
            //달력에서 수정하는 경우(bundle에서 가져온 거 활용)
            else{
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val recordDate = existDiary.recordDate
                    val content: String = binding.diaryDiarycontentEdt.text.toString().trim()
                    val emotionList: List<String> = selectedEmotions.map { it.name }
                    val diaryId = existDiary.id
                    diaryViewModel.editDiary(accessToken, diaryId, recordDate, content, emotionList)
                }
            }


    }

    //diaryWriteAPI 호출
    private fun writeDiaryWithAPI() {
        binding.diaryFinishBtn.isEnabled = false
        binding.diaryFinishBtn.setBackgroundResource(R.drawable.bg_diary_write_no_btn)
        binding.diaryLoadingProgressbar.visibility = View.VISIBLE
        binding.diaryLoadingTv.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            //일단 날짜랑 컨텐츠, 감정을 변수로
            //val recordDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            //    .format(calendar.time)
            val tvWeek = binding.diaryNowdayTv.text.toString()
            val recordDate = formatWeekDayToIso(tvWeek)
            val content: String = binding.diaryDiarycontentEdt.text.toString().trim()
            val emotionList: List<String> = selectedEmotions.map { it.name }

            Log.d("log_diary","일기 작성 날짜: {$recordDate}")
            diaryViewModel.writeDiary(accessToken, recordDate, content, emotionList)

        }
    }


    //bundle에 값 있을 경우 이걸로 하기
    private fun settingUIFromData(){
        var today = existDiary.recordDate
        var content = existDiary.content
        var emotionList = existDiary.emotionList
        var emotions = stringToDiaryEmotions(emotionList)
        
        //1. 날짜 string 바꾸기 (2025-07-24 -> 2025년 07월 24일 목요일)
        val dayFormat = formatIsoToKoreanWithWeekday(today)

        binding.diaryNowdayTv.text = dayFormat

        //2. 내용
        binding.diaryDiarycontentEdt.setText(content)
        
        //3. 이모션 넣기
        selectedEmotions.addAll(emotions)


    }

    //String -> DiaryEmotion
    fun stringToDiaryEmotions(emotionNames: List<String>): Collection<DiaryEmotion> {
        return emotionNames.mapNotNull { name ->
            emotionMap[name]?.let { resId ->
                DiaryEmotion(name, resId)
            }
        }
    }


    //현재 날짜에 따라 텍스트 변경
    private fun updateDateText(){
        //"yyyy년 MM월 dd일 E요일"
        val format = SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.KOREAN)
        val dateString = format.format(calendar.time)
        binding.diaryNowdayTv.text = dateString
    }

    //감정 recyclerview 연결
    private fun setRecyclerview(){

        //일단 임시 dataset
        val tmpemotions = listOf(
            DiaryEmotion( "차분한", R.drawable.ic_emotion1),
            DiaryEmotion( "뿌듯한", R.drawable.ic_emotion2),
            DiaryEmotion( "행복한", R.drawable.ic_emotion3),
            DiaryEmotion( "기대됨", R.drawable.ic_emotion4),
            DiaryEmotion( "설렘", R.drawable.ic_emotion5),
            DiaryEmotion( "감사함", R.drawable.ic_emotion6),
            DiaryEmotion( "신나는", R.drawable.ic_emotion7),
            DiaryEmotion( "슬픈", R.drawable.ic_emotion8),
            DiaryEmotion( "화나는", R.drawable.ic_emotion9),
            DiaryEmotion( "무료함", R.drawable.ic_emotion10),
            DiaryEmotion( "피곤함", R.drawable.ic_emotion11),
            DiaryEmotion( "짜증남", R.drawable.ic_emotion12),
            DiaryEmotion( "외로움", R.drawable.ic_emotion13),
            DiaryEmotion( "우울함", R.drawable.ic_emotion14),
            DiaryEmotion( "스트레스", R.drawable.ic_emotion15),
        )

        //어댑터 설정
        val adaptor = DiaryEmotionAdaptor(tmpemotions, selectedEmotions)
        binding.diaryEmotionRecyclerRcv.apply {
            layoutManager = GridLayoutManager(requireContext(),
                5, RecyclerView.VERTICAL, false)
            this.adapter = adaptor

            //부모 view와 겹쳐서 터치가 씹히는 문제 해결
            isNestedScrollingEnabled = true

            //recyclerview 터치 시 부모가 이벤트를 가로채지 못하게 요청
            setOnTouchListener { _, event ->
                // 터치가 시작될 때만
                if (event.action == MotionEvent.ACTION_DOWN) {
                    binding.diaryScrollview.requestDisallowInterceptTouchEvent(true)
                }
                // false 를 리턴해, 기존 onClickListener도 OK
                false
            }
        }

        //어댑터의 observer 등록 (아이템을 터치할 때 이벤트 발생)
        adaptor.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() = updateConfirmButton(adaptor)
            override fun onItemRangeChanged(start: Int, count: Int) = updateConfirmButton(adaptor)
            override fun onItemRangeInserted(start: Int, count: Int) = updateConfirmButton(adaptor)
            override fun onItemRangeRemoved(start: Int, count: Int)  = updateConfirmButton(adaptor)
        })

        //일단 한 번 체크
        updateConfirmButton(adaptor)

    }

    //adaptor의 getter를 이용해서 추적
    private fun updateConfirmButton(adaptor: DiaryEmotionAdaptor){
        if(adaptor.getSelectedEmotions().isEmpty()){
            binding.diaryFinishBtn.isEnabled = false
            binding.diaryFinishBtn.setBackgroundResource(R.drawable.bg_diary_write_no_btn)
        }
        else{
            binding.diaryFinishBtn.isEnabled = true
            binding.diaryFinishBtn.setBackgroundResource(R.drawable.bg_diary_write_btn)
        }
    }


    //ISO format을 바꿔주기 (2025-07-24 -> 2025년 07월 24일 목요일)
    private fun formatIsoToKoreanWithWeekday(iso: String): String {
        // 1) "yyyy-MM-dd" 분리
        val parts = iso.split("-")
        if (parts.size != 3) return iso

        val year  = parts[0].toIntOrNull() ?: return iso
        val month = parts[1].toIntOrNull() ?: return iso
        val day   = parts[2].toIntOrNull() ?: return iso

        // 2) 캘린더에 세팅 (Asia/Seoul 기준)
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA).apply {
            // month 는 0~11 이므로 -1
            set(year, month - 1, day)
        }

        // 3) 요일명 매핑
        val weekdayNames = arrayOf(
            "일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"
        )
        // Calendar.DAY_OF_WEEK: 1=일요일, 2=월요일, …, 7=토요일
        val weekday = weekdayNames[cal.get(Calendar.DAY_OF_WEEK) - 1]

        // 4) 문자열 포맷팅 (두 자리 맞춤)
        val mm = month.toString().padStart(2, '0')
        val dd = day.toString().padStart(2, '0')
        return "${year}년 ${mm}월 ${dd}일 $weekday"
    }
    
    //WeekDay를 바꿔주기
    private fun formatWeekDayToIso(input: String): String {
        val regex = """(\d{4})년\s*(\d{2})월\s*(\d{2})일""".toRegex()
        val match = regex.find(input) ?: return input

        // ② 그룹에서 year, month, day 꺼내서 재조합
        val (year, month, day) = match.destructured
        return "$year-$month-$day"
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DiaryWriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(content: DiaryDetailResponse) =
            DiaryWriteFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("KEY_RESULT", content)
                }
            }
    }
}