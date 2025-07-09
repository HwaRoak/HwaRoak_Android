package com.example.hwaroak.ui.diary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import com.example.hwaroak.R
import com.example.hwaroak.databinding.FragmentDiaryFinishBinding
import com.example.hwaroak.ui.main.MainActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DiaryFinishFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiaryFinishFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentDiaryFinishBinding

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
        binding = FragmentDiaryFinishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //애니메이션
        binding.diaryFinishResultImv.visibility = TextView.INVISIBLE
        setImageAnimate()


        //일기 다시 작성하기 버튼 누를 시 이동
        binding.diaryFinishModifyDiaryTv.setOnClickListener {
            //일단 안보이게 하기
            binding.diaryFinishResultImv.visibility = TextView.INVISIBLE

            (parentFragment as DiaryFragment)
                .binding
                .diaryViewpager
                .currentItem = 0
        }

        //확인 버튼 시 이동
        binding.diaryFinishFinishBtn.setOnClickListener {
            //메인 액티비티 함수 부르기
            (activity as MainActivity).selectTab(R.id.calendarFragment)
        }

    }



    //중간 이미지에 애니메이션 + 팝 애니메이션
    private fun setImageAnimate() {

        binding.diaryFinishResultImv.visibility = TextView.VISIBLE
        binding.diaryFinishResultImv.scaleX = 0f;
        binding.diaryFinishResultImv.scaleY = 0f;

        //setInterpolator로 애니메이션 진행 속도 조절 + OvershootInterpolator로 튕기는 듯한 (뿅)
        binding.diaryFinishResultImv.post {
            binding.diaryFinishResultImv.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(OvershootInterpolator(2f))
                .setDuration(500)
                .withEndAction {  }
                .start()
        }

    }

    //일기 다시 쓰고 돌아와도 고
    override fun onResume() {
        super.onResume()
        setImageAnimate()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DiaryFinishFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DiaryFinishFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}