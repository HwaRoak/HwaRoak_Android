// HomeFragment.kt
package com.example.hwaroak.ui.main

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.hwaroak.R
import com.example.hwaroak.adaptor.HomeItemRVAdapter
import com.example.hwaroak.data.ItemViewModel
// 다음 import 문들을 추가하거나 확인하세요.
import com.example.hwaroak.api.home.repository.ItemRepository // ItemRepository 임포트
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.HwaRoakClient.questionService
import com.example.hwaroak.api.question.repository.QuestionRepository
import com.example.hwaroak.data.ItemViewModelFactory // ItemViewModelFactory 임포트
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    //rvadapter 선언
    private lateinit var homeItemRVAdapter: HomeItemRVAdapter
    //locker와의 아이템 상태 공유를 위한 viewmodel선언
    private lateinit var itemViewModel: ItemViewModel


    //감정 게이지 바를 위한 pref
    private lateinit var diaryPref: SharedPreferences

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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        diaryPref = requireContext().getSharedPreferences("diary", MODE_PRIVATE)

        /**일단 홈 화면에서는 < 없애기**/
        (activity as? MainActivity)?.setTopBar(isBackVisible = false, true)

        // ItemService 인스턴스를 NetworkModule에서 가져옵니다.
        val itemService = HwaRoakClient.itemApiService // NetworkModule.kt에 정의된 itemApiService 사용
        val questionService = HwaRoakClient.questionService

        // ItemRepository 인스턴스를 ItemService와 함께 생성합니다.
        val itemRepository = ItemRepository(itemService) // ItemRepository 생성자에 itemService 전달
        val questionRepository = QuestionRepository(questionService)

        // ViewModel 초기화: ItemViewModelFactory를 사용하여 ItemRepository를 주입합니다.
        itemViewModel = ViewModelProvider(
            requireActivity(),
            ItemViewModelFactory(itemRepository, questionRepository)
        ).get(ItemViewModel::class.java) //


        //RecyclerView 어뎁터 설정
        val recyclerView = view.findViewById<RecyclerView>(R.id.home_item_rv)
        homeItemRVAdapter = HomeItemRVAdapter()
        recyclerView.adapter = homeItemRVAdapter

        //감정 게이지바 세팅
        setEmotionBar()

        // 아이템에 따른 캐릭터 멘트 변경 위해 tv_speech_bubble 참조 가져오기
        val speechBubbleTV = view.findViewById<TextView>(R.id.tv_speech_bubble)
        itemViewModel.speech.observe(viewLifecycleOwner) { text ->
            speechBubbleTV.text = text
        }

        //참조 가져오기
        val rewardContainer = view.findViewById<LinearLayout>(R.id.reward_container)
        val rewardItemsRV = view.findViewById<RecyclerView>(R.id.rv_reward_items)
        val btnClaimReward = view.findViewById<MaterialButton>(R.id.btn_claim_reward)

        val rewardItemRVAdapter = HomeItemRVAdapter()
        rewardItemsRV.adapter = rewardItemRVAdapter

        //보상할 아이템이 있을 때만 reward영역 보여주기
        itemViewModel.rewardAvailable.observe(viewLifecycleOwner) { canReward ->
            rewardContainer.visibility = if (canReward == true) View.VISIBLE else View.GONE
        }
        
        // 보상처리
        btnClaimReward.setOnClickListener {
            itemViewModel.claimReward { rewardedItem ->
                if (rewardedItem != null) {
                    // 캐릭터 말풍선 변경
//                    speechBubbleTV.text = "보상이야!(추후매핑)"
                    itemViewModel.clearRewardAfterClaim()
                    // 보상 UI 숨기고 보상완료 UI 표시
                    rewardContainer.visibility = View.GONE
                    val rewardCompletionContainer = view.findViewById<LinearLayout>(R.id.reward_completion_container)
                    rewardCompletionContainer.visibility = View.VISIBLE
                    // 2초 후 다시 기본 홈 상태로 되돌리기
                    view.postDelayed({
                        rewardCompletionContainer.visibility = View.GONE
                    }, 2000)
                } else {
                    // 실패 시 처리
                    Toast.makeText(requireContext(), "보상받기 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }


        // 감정 버튼 참조 가져오기
        val btnSad = view.findViewById<MaterialButton>(R.id.btn_sad)
        val btnAnnoyed = view.findViewById<MaterialButton>(R.id.btn_annoyed)
        val btnJoy = view.findViewById<MaterialButton>(R.id.btn_joy)
        val btnDepress = view.findViewById<MaterialButton>(R.id.btn_depress)
        val btnExcited = view.findViewById<MaterialButton>(R.id.btn_excited)
        val btnHappy = view.findViewById<MaterialButton>(R.id.btn_happy)

        // 아이템 이름 -> 이미지 뷰 매핑
        val itemImageMap = mapOf(
            "default" to view.findViewById<ImageView>(R.id.iv_character),
            "cheeze" to view.findViewById<ImageView>(R.id.iv_hwaroak_item_cheeze),
            "chicken" to view.findViewById<ImageView>(R.id.iv_hwaroak_item_chicken),
            "chopstick" to view.findViewById<ImageView>(R.id.iv_hwaroak_item_chopstick),
            "coal" to view.findViewById<ImageView>(R.id.iv_hwaroak_item_coal),
            "cup" to view.findViewById<ImageView>(R.id.iv_hwaroak_item_cup),
            "egg" to view.findViewById<ImageView>(R.id.iv_hwaroak_item_egg),
            "mashmellow" to view.findViewById<ImageView>(R.id.iv_hwaroak_item_mashmellow),
            "meat" to view.findViewById<ImageView>(R.id.iv_hwaroak_item_meat),
            "paper" to view.findViewById<ImageView>(R.id.iv_hwaroak_item_paper),
            "potato" to view.findViewById<ImageView>(R.id.iv_hwaroak_item_potato),
            "ruby" to view.findViewById<ImageView>(R.id.iv_hwaroak_item_ruby),
            "soup" to view.findViewById<ImageView>(R.id.iv_hwaroak_item_soup),
            "tire" to view.findViewById<ImageView>(R.id.iv_hwaroak_item_tire),
            "tissue" to view.findViewById<ImageView>(R.id.iv_hwaroak_item_tissue),
            "trash" to view.findViewById<ImageView>(R.id.iv_hwaroak_item_trash)
        )

        // 아이템 이름 -> 멘트 매핑(랜덤 매핑을 위해 리스트로)
        val itemSpeechMap: Map<String, List<String>> = mapOf(
            "default" to listOf("무슨 일이 있어?", "오늘 하루도 화이팅!", "심심한데 놀아줄래?"),
            "cheeze" to listOf("난 모짜렐라가 더 좋아!", "치즈는 사랑이야!", "냠냠, 치즈 맛이 좋아~"),
            "chicken" to listOf("치킨이 최고야! 한 입 할래?", "오늘 저녁은 치킨이닭!", "바삭바삭 치킨!"),
            "chopstick" to listOf("젓가락으로 뭘 먹을까?", "젓가락질 잘해야만 밥 잘 먹나요~", "젓가락을 잘 쪼개봐!"),
            "coal" to listOf("따뜻한 연료가 필요해?", "연탄재 함부로 발로 차지 마라", "캠핑 갈 때 유용하겠지?"),
            "cup" to listOf("마실게 필요해?", "시원한 물 한 잔 어때?", "컵에 뭘 담을까?"),
            "egg" to listOf("계란은 역시 삶아야 제맛!", "난 캘시퍼는 아니야", "맛있는 계란 요리 해줄까?"),
            "mashmellow" to listOf("말랑말랑 마시멜로우~", "쫀득쿠키 만들자!", "달콤한 마시멜로우!"),
            "meat" to listOf("고기는 언제나 옳지!", "내가 고기를 구워봤어!", "힘이 솟아나는 고기!"),
            "paper" to listOf("종이에 뭘 그려볼까?", "이건 목재야! 골판지가 아니야", "편지 쓸까?"),
            "potato" to listOf("포슬포슬 감자! 구워 먹을까?", "난 찐감자가 좋아!", "든든한 감자!"),
            "ruby" to listOf("드디어 최종 보상이야!"),
            "soup" to listOf("따뜻한 수프 한 그릇 어때?", "몸이 녹는 것 같아~", "우와 맛있어!!"),
            "tire" to listOf("타이어 화록보다 싸다!", "슝슝~ 달려볼까?", "튼튼한 타이어!"),
            "tissue" to listOf("두루마리 휴지야", "어디에 쓸까?", "쓱싹쓱싹!"),
            "trash" to listOf("내가 좋아하는 쓰레기 봉투야!", "지구를 깨끗하게!", "분리수거 잘 해야 해!")
        )

        // 감정 버튼 멘트 매핑
        val emotionSpeechMap: Map<String, List<String>> = mapOf(
            "sad" to listOf("오늘 무슨 일이 있었던 걸까...?", "나한테 털어놔도 괜찮아.", "오늘은 불을 작게....."),
            "annoyed" to listOf("으으, 뭔가 억울한 일이 있었던 거야?", "폭발 직전이야!!", "조심해!! 곧 터진다!!", "석탄으로 만들어주겠어!"),
            "joy" to listOf("좋은 일이 있었나봐!", "이런 날은 꼭 기록해두자!"),
            "depress" to listOf("그 감정, 혼자 꾹 참고 있진 않았으면 좋겠어.", "무슨 일이야??"),
            "excited" to listOf("마음이 두근거리는 하루였구나!", "나도 너무 궁금해!", "나도 더 타오를 수 있을까?"),
            "happy" to listOf("이 순간이 오래 기억되면 좋겠어.", "이러다가 내가 다 타버리겠어!!")
        )

        //ViewModel 감지해 RecyclerView 갱신
        itemViewModel.homeItemList.observe(viewLifecycleOwner) { newList ->
            homeItemRVAdapter.setData(newList)

            // 모든 아이템 든 캐릭터 이미지 숨기기
            itemImageMap.values.forEach { it.visibility = View.GONE }

            val selectedItem = newList.firstOrNull()
            val currentItemName = selectedItem?.name ?: "default" // 선택된 아이템 이름이 없으면 "default" 사용

            // 이미지뷰 가시성 설정
            itemImageMap[currentItemName]?.visibility = View.VISIBLE

            // 텍스트뷰 멘트 설정 (랜덤 선택)
            val speechOptions = itemSpeechMap[currentItemName] // 해당 아이템의 멘트 리스트 가져오기
            if (!speechOptions.isNullOrEmpty()) { // 리스트가 비어있지 않다면
                val randomSpeech = speechOptions.random() // 리스트에서 랜덤으로 하나 선택
                speechBubbleTV.text = randomSpeech
            } else {
                speechBubbleTV.text = "무슨 일이 있어?" // 멘트 리스트가 없거나 비어있으면 기본 멘트
            }
        }

        // 감정 버튼 클릭 리스너
        btnSad.setOnClickListener {
            val speechOptions = emotionSpeechMap["sad"]
            if (!speechOptions.isNullOrEmpty()) {
                speechBubbleTV.text = speechOptions.random()
            }
        }

        btnAnnoyed.setOnClickListener {
            val speechOptions = emotionSpeechMap["annoyed"]
            if (!speechOptions.isNullOrEmpty()) {
                speechBubbleTV.text = speechOptions.random()
            }
        }

        btnJoy.setOnClickListener {
            val speechOptions = emotionSpeechMap["joy"]
            if (!speechOptions.isNullOrEmpty()) {
                speechBubbleTV.text = speechOptions.random()
            }
        }

        btnDepress.setOnClickListener {
            val speechOptions = emotionSpeechMap["depress"]
            if (!speechOptions.isNullOrEmpty()) {
                speechBubbleTV.text = speechOptions.random()
            }
        }

        btnExcited.setOnClickListener {
            val speechOptions = emotionSpeechMap["excited"]
            if (!speechOptions.isNullOrEmpty()) {
                speechBubbleTV.text = speechOptions.random()
            }
        }

        btnHappy.setOnClickListener {
            val speechOptions = emotionSpeechMap["happy"]
            if (!speechOptions.isNullOrEmpty()) {
                speechBubbleTV.text = speechOptions.random()
            }
        }
    }

    //홈 게이지 바 설정
    private fun setEmotionBar(){
        val emotionGauge = view?.findViewById<ImageView>(R.id.emotion_gauge_home)
        val isWrite = diaryPref.getBoolean("isWrite", false)

        //일기 안 썼어
        if(!isWrite){
            emotionGauge!!.setImageResource(R.drawable.img_home_emotion_gauge_default)
        }
        else{
            //기준 0=기본, 1=긍정ALL, 2=긍정/부정 반반, 3=부정부정긍정, 4=부정긍정긍정, 5=부정ALL
            val barType = diaryPref.getInt("barType", 0)
            if(barType == 0) {emotionGauge!!.setImageResource(R.drawable.img_home_emotion_gauge_default)}
            else if(barType == 1) {emotionGauge!!.setImageResource(R.drawable.img_home_emotion_gauge_all_pos)}
            else if(barType == 2) {emotionGauge!!.setImageResource(R.drawable.img_home_emotion_gauge_half_half)}
            else if(barType == 3) {emotionGauge!!.setImageResource(R.drawable.img_home_emotion_gauge_neg_neg_pos)}
            else if(barType == 4) {emotionGauge!!.setImageResource(R.drawable.img_home_emotion_gauge_neg_pos_pos)}
            else if(barType == 5) {emotionGauge!!.setImageResource(R.drawable.img_home_emotion_gauge_all_neg)}
        }

    }

    override fun onResume() {
        super.onResume()
        setEmotionBar()

        val pref = requireContext().getSharedPreferences("member", MODE_PRIVATE) // 실제 키/이름 사용
        val token = pref.getString("accessToken", null) ?: return
        lifecycleScope.launch {
            itemViewModel.refreshQuestion(token)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}