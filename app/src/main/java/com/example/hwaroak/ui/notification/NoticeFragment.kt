package com.example.hwaroak.ui.notification

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hwaroak.R
import com.example.hwaroak.adaptor.NoticeItemRVAdaptor
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.notice.access.NoticeViewModel
import com.example.hwaroak.api.notice.access.NoticeViewModelFactory
import com.example.hwaroak.api.notice.repository.NoticeRepository
import com.example.hwaroak.data.NoticeItem
import com.example.hwaroak.databinding.FragmentNoticeBinding
import com.example.hwaroak.ui.diary.DiaryFragment
import com.example.hwaroak.ui.friend.FriendFragment
import com.example.hwaroak.ui.main.MainActivity
import com.example.hwaroak.ui.mypage.AnalysisFragment
import com.example.hwaroak.ui.mypage.AnnouncementFragment
import com.example.hwaroak.ui.mypage.SettingFragment
import kotlin.getValue

class NoticeFragment : Fragment() {

    private var _binding: FragmentNoticeBinding? = null
    private val binding get() = _binding!!

    //엑세스 토큰 받기
    private lateinit var pref: SharedPreferences
    private lateinit var accessToken: String

    //viewModel 및 repository 정의
    //viewModel 정의
    // 1) Retrofit 서비스로 Repository 인스턴스 생성
    private val noticeRepository by lazy {
        NoticeRepository(HwaRoakClient.noticeService)
    }
    // 2) Activity 스코프로 ViewModel 생성 (Factory 주입)
    private val noticeViewModel: NoticeViewModel by viewModels{
        NoticeViewModelFactory(noticeRepository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //초기 설정
        pref = requireContext().getSharedPreferences("user", MODE_PRIVATE)
        accessToken = pref.getString("accessToken", "").toString()
        
        /**알람함 observe**/
        noticeViewModel.alarmList.observe(viewLifecycleOwner) { result ->
            result.onSuccess { resp ->
                val noticeList = resp.map { item ->
                    NoticeItem(item.id, item.title, item.content,
                        item.alarmType, item.isRead, item.createdAt)
                }

                //알림 recyclerview 어댑터 선언, 설정, inflate
                val adapter = NoticeItemRVAdaptor(noticeList, accessToken)
                //콜백 함수 정의
                {
                    selectedNotice ->
                    //불 키우기
                    if(selectedNotice.alarmType == "FIRE"){
                        (activity as? MainActivity)?.selectTab(R.id.friendFragment)
                    }
                    //월간 리포트
                    else if(selectedNotice.alarmType == "DAILY"){
                        (activity as? MainActivity)?.selectTab(R.id.mypageFragment)
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.main_fragmentContainer, AnalysisFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                    //일기 리마인더
                    else if(selectedNotice.alarmType == "REMINDER"){
                        (activity as? MainActivity)?.selectTab(R.id.diaryFragment)
                    }
                    //공지 사항
                    else if(selectedNotice.alarmType == "NOTIFICATION"){
                        (activity as? MainActivity)?.selectTab(R.id.mypageFragment)
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.main_fragmentContainer, AnnouncementFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                    //친구 요청
                    else if(selectedNotice.alarmType == "FRIEND_REQUEST"){
                        (activity as? MainActivity)?.selectTab(R.id.friendFragment)
                    }

                }
                binding.rvNoticeContainer.adapter = adapter
                binding.rvNoticeContainer.layoutManager = LinearLayoutManager(requireContext())

            }.onFailure { err ->
                Toast.makeText(
                    requireContext(),
                    "알람 로드 중 오류 발생: {$err}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //호출
        noticeViewModel.getAlarmList(accessToken)

        binding.noticeBackBtn.setOnClickListener {
            // 뒤로가기 버튼 클릭 시 MainActivity의 상단 바를 다시 보이게 함
            (activity as? MainActivity)?.showMainTopBar()
            // HomeFragment로 이동하거나 백 스택에서 현재 프래그먼트를 제거
            (activity as? MainActivity)?.selectTab(R.id.homeFragment)
        }
        // 추후 필요시 여기에 다른 로직 구현

        //1. 알림 설정 누를 시 해당 화면으로 이동
        binding.btnNoticeSetting.setOnClickListener {
            // 알림 설정 화면으로 이동
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentContainer, SettingFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideMainTopBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 프래그먼트 뷰가 파괴될 때 MainActivity의 상단 바를 다시 보이게 함(프래그먼트가 완전히 제거될 때 호출)
        (activity as? MainActivity)?.showMainTopBar()
        _binding = null
    }
}