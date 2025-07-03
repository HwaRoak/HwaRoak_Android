package com.example.hwaroak.ui.diary

import android.app.DatePickerDialog
import android.content.Intent
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
import com.example.hwaroak.data.DiaryEmotion
import com.example.hwaroak.databinding.FragmentDiaryBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DiaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiaryFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentDiaryBinding

    //선택된 item view들의 집합
    private val selectedEmotions = mutableSetOf<DiaryEmotion>()

    //달력 개체
    private val calendar = Calendar.getInstance()

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
        binding = FragmentDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 맨 처음 날짜 표시
        updateDateText()
        //recyclerviwe 연결
        setRecyclerview()

        // 이전 날짜 버튼 클릭 리스너 설정
        binding.diaryDateprevBtn.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            updateDateText()
        }

        // 다음 날짜 버튼 클릭 리스너 설정
        binding.diaryDatenextBtn.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            updateDateText()
        }

        // 날짜 직접 선택 클릭 리스너 설정
        binding.diaryNowdayTv.setOnClickListener {
            pickDayFromCalendar()
        }

        // 다이어리 작성 버튼 클릭 리스너 설정
        binding.diaryWriteDiarySwapBtn.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.diaryWriteDiaryContentCdv)
            val isVisible = binding.diaryWriteDiaryContentCdv.isVisible
            binding.diaryWriteDiaryContentCdv.visibility = if (isVisible) View.GONE else View.VISIBLE

            val angle = if (isVisible) 180f else 0f
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

            val angle = if (isVisible) 180f else 0f
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

            val intent = Intent(requireContext(), DiaryFinishActivity::class.java)
            startActivity(intent)
        }

    }

    //현재 날짜에 따라 텍스트 변경
    private fun updateDateText(){
        //"yyyy년 MM월 dd일 E요일"
        val format = SimpleDateFormat("MM월 dd일 E요일", Locale.KOREAN)
        val dateString = format.format(calendar.time)
        binding.diaryNowdayTv.text = dateString
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

    //감정 recyclerview 연결
    private fun setRecyclerview(){

        //일단 임시 dataset
        val tmpemotions = listOf(
            DiaryEmotion( "차분한1", R.drawable.ic_emotion1),
            DiaryEmotion( "차분한2", R.drawable.ic_emotion1),
            DiaryEmotion( "차분한3", R.drawable.ic_emotion1),
            DiaryEmotion( "차분한4", R.drawable.ic_emotion1),
            DiaryEmotion( "차분한5", R.drawable.ic_emotion1),
            DiaryEmotion( "차분한6", R.drawable.ic_emotion1),
            DiaryEmotion( "차분한7", R.drawable.ic_emotion1),
            DiaryEmotion( "차분한8", R.drawable.ic_emotion1),
            DiaryEmotion( "차분한9", R.drawable.ic_emotion1),
            DiaryEmotion( "차분한10", R.drawable.ic_emotion1),
            DiaryEmotion( "차분한11", R.drawable.ic_emotion1),
            DiaryEmotion( "차분한12", R.drawable.ic_emotion1),
            DiaryEmotion( "차분한13", R.drawable.ic_emotion1),
            DiaryEmotion( "차분한14", R.drawable.ic_emotion1),
            DiaryEmotion( "차분한15", R.drawable.ic_emotion1),
            )

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

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DiaryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DiaryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}