package com.example.hwaroak.ui.diary

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.transition.TransitionManager
import com.example.hwaroak.R
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