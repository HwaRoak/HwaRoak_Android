package com.example.hwaroak.ui.diary

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.hwaroak.R
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.diary.access.DiaryViewModel
import com.example.hwaroak.api.diary.access.DiaryViewModelFactory
import com.example.hwaroak.api.diary.repository.DiaryRepository
import com.example.hwaroak.databinding.FragmentDiaryFinishBinding
import com.example.hwaroak.ui.main.MainActivity
import com.kakao.sdk.common.util.SharedPrefsWrapper
import kotlin.getValue

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
    private lateinit var diaryPref: SharedPreferences

    //긍정 = 1 / 부정 = -1
    private var emotionMap : MutableMap<String, Int> = mutableMapOf(
        "차분한" to 1,
   "뿌듯한" to 1, "행복한" to 1, "기대됨" to 1, "설렘" to 1, "감사함" to 1, "신나는" to 1,
    "슬픈" to -1, "화나는" to -1, "무료함" to -1, "피곤함" to -1, "짜증남" to -1, "외로움" to -1, "우울함" to -1, "스트레스" to -1,
    )

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

    //아이템 map
    private val rewardItemList: MutableMap<Int, Int> = mutableMapOf(
        1 to R.drawable.img_item_tissue
    )

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
        diaryPref = requireContext().getSharedPreferences("diary", MODE_PRIVATE)

        //애니메이션
        binding.diaryFinishResultImv.visibility = TextView.INVISIBLE
        binding.diaryFinishFire1Imv.visibility= TextView.INVISIBLE
        binding.diaryFinishFire2Imv.visibility = TextView.INVISIBLE
        binding.diaryFinishFire3Imv.visibility = TextView.INVISIBLE
        setImageAnimate()
        //settingUIWithViewModel()



        // “쓰기” 모드 결과를 받으면 UI 갱신
        diaryViewModel.writeResult.observe(viewLifecycleOwner) { result ->
            // isWrite 가 1(쓰기) 일 때만 처리
            if (diaryViewModel.isWrite.value == 1) {
                Log.d("log_diary", "------\n쓰기 옵저버 통과")
                result.onSuccess { resp ->
                    settingDiaryResult(resp.reward, resp.memberItemId, resp.id, resp.emotionList)
                    applyUi(resp.feedback, resp.reward, resp.memberItemId, resp.id)
                }
                // onFailure 도 여기서 Toast 등으로 처리 가능
            }
        }

        // “수정” 모드 결과를 받으면 UI 갱신
        diaryViewModel.editResult.observe(viewLifecycleOwner) { result ->
            if (diaryViewModel.isWrite.value == 2) {
                Log.d("log_diary", "------\n수정 옵저버 통과")
                result.onSuccess { resp ->
                    settingDiaryResult(resp.reward, resp.memberItemId, resp.id, resp.emotionList)
                    applyUi(resp.feedback, resp.reward, resp.memberItemId, resp.id)
                }
            }
        }



        //일기 다시 작성하기 버튼 누를 시 이동
        binding.diaryFinishModifyDiaryTv.setOnClickListener {
            //일단 안보이게 하기
            binding.diaryFinishResultImv.visibility = TextView.INVISIBLE
            binding.diaryFinishFire1Imv.visibility= TextView.INVISIBLE
            binding.diaryFinishFire2Imv.visibility = TextView.INVISIBLE
            binding.diaryFinishFire3Imv.visibility = TextView.INVISIBLE

            //수정 모드로 체인지
            diaryViewModel.setEditMode() // isWrite = 2
            diaryViewModel.isEditCalendar = false

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

    //sharedPref에 일기 결과를 임시 저장하는 곳(감정 및 간단 정보(리워드 등))
    private fun settingDiaryResult(reward: Int, itemId: Int, dirayId: Int, emotionList: List<String>){

        //일단 감정 처리(0-5) [기본, 초긍정, 긍긍부, 부부긍, 초부정]
        var barType : Int = 0
        //1. 감정이 1개
        if(emotionList.size == 1){
            if(emotionMap.get(emotionList.get(0)) == 1){ barType = 1 }
            else{ barType = 5 }
        }
        else if(emotionList.size == 2){
            val tmp = emotionMap.get(emotionList.get(0))!! + emotionMap.get(emotionList.get(1))!!
            if(tmp == 2){barType = 1}
            else if(tmp == 0){barType = 2}
            else{barType = 5}
        }
        else if(emotionList.size == 3){
            val tmp = emotionMap.get(emotionList.get(0))!! + emotionMap.get(emotionList.get(1))!! + emotionMap.get(emotionList.get(2))!!
            if(tmp == 3){barType = 1}
            else if(tmp == 1){barType = 3}
            else if(tmp == -1){barType = 4}
            else{barType = 5}
        }

        //리워드 남을 날짜, 아이템 ID, 다이어리 ID, 해당 다이어리의 감정 바 타입
        diaryPref.edit{
            putInt("reward", reward)
            putInt("itemId", itemId)
            putInt("diaryId", dirayId)
            putInt("barType", barType)
                .apply()
        }
        Log.d("log_diary", "barType = $barType")
        Log.d("log_diary", "itemId = $itemId")
        Log.d("log_diary", "diaryId = $dirayId")
        Log.d("log_diary", "reward = $reward")
    }

    private fun applyUi(feedback: String, reward: Int, itemId: Int, dirayId: Int) {
        binding.diaryFinishHwatextTv.text    = feedback
        binding.diaryFinishRewardTv.text     = "리워드까지 ${reward}일!"
        binding.diaryFinishRew1Imv.setImageResource(
            when (itemId) {
                1 -> R.drawable.img_item_tissue
                // …다른 아이템 매핑
                else -> R.drawable.img_item_tissue
            }
        )
        diaryViewModel.lastDiaryId = dirayId
    }




    //중간 이미지에 애니메이션 + 팝 애니메이션
    private fun setImageAnimate() {

        //중앙 이미지 초기세팅
        binding.diaryFinishResultImv.visibility = TextView.VISIBLE
        binding.diaryFinishResultImv.scaleX = 0f;
        binding.diaryFinishResultImv.scaleY = 0f;

        //옆에 불들 초기세팅
        val fire1 = binding.diaryFinishFire1Imv
        val fire2 = binding.diaryFinishFire2Imv
        val fire3 = binding.diaryFinishFire3Imv
        listOf(fire1, fire2, fire3).forEach { view ->
            view.visibility = View.VISIBLE
            view.scaleX = 0f
            view.scaleY = 0f
        }
        

        //setInterpolator로 애니메이션 진행 속도 조절 + OvershootInterpolator로 튕기는 듯한 (뿅)
        binding.diaryFinishResultImv.post {
            binding.diaryFinishResultImv.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(OvershootInterpolator(2f))
                .setDuration(500)
                .withEndAction { setFireAnimate() }
                .start()

        }

    }

    private fun setFireAnimate(){
        val fire1 = binding.diaryFinishFire1Imv
        val fire2 = binding.diaryFinishFire2Imv
        val fire3 = binding.diaryFinishFire3Imv
      
        //애니메이션 시간 및 지연시간
        val duration = 300L
        val delay = 150L

        fire1.post {
            fire1.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(OvershootInterpolator(2f))
                .setDuration(duration)
                .setStartDelay(0)
                .start()
        }
        fire2.post {
            fire2.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(OvershootInterpolator(2f))
                .setDuration(duration)
                .setStartDelay((duration / 2) + delay)
                .start()
        }
        fire3.post {
            fire3.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(OvershootInterpolator(2f))
                .setDuration(duration)
                .setStartDelay(duration + delay)
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