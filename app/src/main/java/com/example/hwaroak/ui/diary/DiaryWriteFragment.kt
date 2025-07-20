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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.example.hwaroak.R
import com.example.hwaroak.adaptor.DiaryEmotionAdaptor
import com.example.hwaroak.data.DiaryContent
import com.example.hwaroak.data.DiaryEmotion
import com.example.hwaroak.databinding.FragmentDiaryWriteBinding
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    private lateinit var binding: FragmentDiaryWriteBinding

    //선택된 item view들의 집합
    private val selectedEmotions = mutableSetOf<DiaryEmotion>()

    //달력 개체
    private val calendar = Calendar.getInstance()

    //수정 모드인지 아니면 새로 쓰는지 확인
    private var isEditMode = false
    private lateinit var diaryContent : DiaryContent

    //엑세스 토큰
    //유저 정보를 담을 sharedPreference
    private lateinit var pref: SharedPreferences
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //fragment 인자로 받았는지 확인
        arguments?.getParcelable<DiaryContent>("KEY_RESULT")?.let {
            isEditMode = true
            diaryContent = it
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

        //맨 처음 날짜 표시
        updateDateText()

        //만약 bundle로 가져올 경우
        //1. 일단 calender에서 온 것이니 날짜 최신화 체크
        //2. 만약 emotion이 1개 이상이면 기존 꺼 수정이니 수정 check
        if(isEditMode){
            Log.d("log_diary", "Edit Mode IN")
            settingUIFromData()
            if(diaryContent.emotions.size>0){
                binding.diaryFinishOrEditTv.text = "수정하기"
            }
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

            Log.d("log_diary", calendar.time.toString())
            Log.d("log_diary", selectedEmotions.toString())
            Log.d("log_diary", binding.diaryDiarycontentEdt.text.toString())

            (parentFragment as DiaryFragment)
                .binding
                .diaryViewpager
                .currentItem = 1
        }

    }

    //bundle에 값 있을 경우 이걸로 하기
    private fun settingUIFromData(){
        var today = diaryContent.date
        var content = diaryContent.content
        var emotions = diaryContent.emotions
        
        //1. 날짜 string
        val dfm = DateTimeFormatter.ofPattern("MM월 dd일 E요일", Locale.KOREAN)
        val dayFormat = today.date.format(dfm)
        Log.d("log_diary", dayFormat)

        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR,  today.year)
            set(Calendar.MONTH, today.month - 1)
            set(Calendar.DAY_OF_MONTH, today.day)
        }
        binding.diaryNowdayTv.text = dayFormat

        //2. 내용
        binding.diaryDiarycontentEdt.setText(content)
        
        //3. 이모션 넣기
        selectedEmotions.addAll(emotions)


    }

    //현재 날짜에 따라 텍스트 변경
    private fun updateDateText(){
        //"yyyy년 MM월 dd일 E요일"
        val format = SimpleDateFormat("MM월 dd일 E요일", Locale.KOREAN)
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

    //달력 다이얼로그를 열어서 날짜 선택
    private fun pickDayFromCalendar(){
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        //기존 지원 달력 다이얼로그 활용
        //콜백 함수를 통해 선택한 y,m,d를 get해서 calendar 객체에 적용
        DatePickerDialog(requireContext(), { _, y, m, d ->
            calendar.set(Calendar.YEAR, y)
            calendar.set(Calendar.MONTH, m)
            calendar.set(Calendar.DAY_OF_MONTH, d)
            updateDateText()
        }, year, month, day).show()
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
        fun newInstance(content: DiaryContent) =
            DiaryWriteFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("KEY_RESULT", content)
                }
            }
    }
}