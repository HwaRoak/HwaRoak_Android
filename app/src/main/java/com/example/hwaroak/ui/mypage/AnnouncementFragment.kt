package com.example.hwaroak.ui.mypage

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hwaroak.adaptor.AnnouncementRVAdaptor
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.diary.access.DiaryViewModel
import com.example.hwaroak.api.diary.access.DiaryViewModelFactory
import com.example.hwaroak.api.diary.repository.DiaryRepository
import com.example.hwaroak.api.notice.access.NoticeViewModel
import com.example.hwaroak.api.notice.access.NoticeViewModelFactory
import com.example.hwaroak.api.notice.repository.NoticeRepository
import com.example.hwaroak.data.AnnouncementData
import com.example.hwaroak.databinding.FragmentAnnouncementBinding
import com.example.hwaroak.ui.main.MainActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.getValue

class AnnouncementFragment : Fragment() {

    private var _binding: FragmentAnnouncementBinding? = null
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
    ): View {
        _binding = FragmentAnnouncementBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //초기 설정
        pref = requireContext().getSharedPreferences("user", MODE_PRIVATE)
        accessToken = pref.getString("accessToken", "").toString()

        (activity as? MainActivity)?.setTopBar("공지사항", isBackVisible = true)

        /**공지 observer**/
        noticeViewModel.noticeList.observe(viewLifecycleOwner) { result ->
            result.onSuccess { resp ->
                //resp = List<NoticeResponse>
                //공지 조회 -> 공지 상세 조회로 이동 (별도의 쓰레드에서 시작)
                viewLifecycleOwner.lifecycleScope.launch {
                    //아얘 바로 recyclerview에 넣을 수 있게 한 번에 수행 후 묶기
                    val announcementItems = coroutineScope {
                        //map을 이용해 각 for each 돌며 최종적으로 list 수집
                        resp.map {item ->
                            async {
                                //비동기로 각 공지에 대해 상세 정보 get
                                val detailResult = noticeViewModel.getDetailNoticeAsync(accessToken, item.id)
                                AnnouncementData(
                                    item.id, item.title,
                                    detailResult?.content ?: "", item.createdAt, false
                                    )
                            }
                        }
                    }.awaitAll() // 다 기다려라 -> 최종적으로 List<AnnouncementData>

                    binding.announcementRv.layoutManager = LinearLayoutManager(requireContext())
                    binding.announcementRv.adapter = AnnouncementRVAdaptor(announcementItems)

                }
            }.onFailure { err ->
                Toast.makeText(
                    requireContext(),
                    "공지 로드 중 오류 발생: {$err}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        //API 호출 -> 자동으로 liveData observe하면서 촤라락
        noticeViewModel.getNoticeList(accessToken)

    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}