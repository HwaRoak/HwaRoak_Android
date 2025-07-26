package com.example.hwaroak.ui.mypage

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.animate
import com.example.hwaroak.R
import com.example.hwaroak.databinding.DialogLogoutCheckBinding
import com.example.hwaroak.databinding.FragmentAnnouncementBinding
import com.example.hwaroak.databinding.FragmentMypageBinding
import com.example.hwaroak.ui.friend.AddFriendFragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class MypageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        // 원형 그래프 함수 호출
        initPieChart()

        // 내정보 버튼 클릭시 해당 프래그먼트로 전환
        binding.btnMyinfo.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentContainer, EditProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        // 상세보기 버튼 클릭시 해당 프래그먼트로 전환
        binding.btnCheckDetail.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentContainer, AnalysisFragment())
                .addToBackStack(null)
                .commit()
        }

        // 공지사항 글자 클릭시 해당 프래그먼트로 전환
        binding.announcement.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentContainer, AnnouncementFragment())
                .addToBackStack(null)
                .commit()
        }

        // 알림설정 글자 클릭시 해당 프래그먼트로 전환
        binding.notificationSetting.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentContainer, SettingFragment())
                .addToBackStack(null)
                .commit()
        }

        // 약관 및 정책 글자 클릭시 해당 프래그먼트로 전환
        binding.terms.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentContainer, TermsFragment())
                .addToBackStack(null)
                .commit()
        }

        // 유저 검색 글자 클릭시 해당 프래그먼트로 전환
        binding.searchUser.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentContainer, AddFriendFragment())
                .addToBackStack(null)
                .commit()
        }

        // 로그아웃 글자 클릭 시 다이얼로그 띄우게 구현 예정
        binding.logout.setOnClickListener {
            showLogoutDialog()
        }

        return binding.root
    }

    // 원형 그래프 함수
    private fun initPieChart() {
        // 그래프 데이터 비율
        val emotionRatio = listOf(
            PieEntry(25f),
            PieEntry(75f),
            PieEntry(0f),
            PieEntry(0f)
        )

        // 그래프 데이터별 색상 지정
        val pieColors = listOf(
            ContextCompat.getColor(requireContext(), R.color.comfy),
            ContextCompat.getColor(requireContext(), R.color.happy),
            ContextCompat.getColor(requireContext(), R.color.depressed),
            ContextCompat.getColor(requireContext(), R.color.angry)
        )

        // 데이터별 스타일링을 위해 DataSet 생성(label은 필요 없어서 공백으로 둠)
        val dataSet = PieDataSet(emotionRatio, "")

        // slice의 색상을 설정해준다.
        dataSet.colors = pieColors

        // 지금 서비스에서는 그래프에 퍼센트로 표시하지 않으므로 false
        dataSet.setDrawValues(false)

        binding.emotionPiechart.apply {
            data = PieData(dataSet)

            // description.isEnabled : 차트 설명 유무 설정
            // legend.isEnabled : 범례 유무 설정
            // isRotationEnabled : 차트 회전 활성화 여부 설정
            // holeRadius : 차트 중간 구멍 크기 설정
            // setTouchEnabled : slice 터치 활성화 여부 설정
            // animateY(1200, Easing.EaseInOutCubic) : 애니메이션 시간, 효과 설정
            description.isEnabled = false
            legend.isEnabled = false
            isRotationEnabled = true
            holeRadius = 55f
            setTouchEnabled(false)
            // animateY(1200, Easing.EaseInOutCubic)

            animate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLogoutDialog() {
        // 1. 다이얼로그 레이아웃으로 뷰 바인딩 객체 생성
        val dialogBinding = DialogLogoutCheckBinding.inflate(LayoutInflater.from(requireContext()))

        // 2. AlertDialog 생성
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setView(dialogBinding.root) // 뷰 바인딩의 root 뷰를 설정
            .create()

        // 3. 취소 버튼 리스너와 로그아웃 버튼 리스너
        dialogBinding.dialogCancelBtn.setOnClickListener {
            dialog.dismiss() // 다이얼로그 닫기
        }

        dialogBinding.dialogLogoutBtn.setOnClickListener {
            // 여기에 로직 짜주시면 될 것 같습니다!!

            // 로직 처리 후 다이얼로그 닫기
            dialog.dismiss()
        }

        // 4. 다이얼로그 표시
        dialog.show()
    }
}