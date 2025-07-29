package com.example.hwaroak.ui.mypage

import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.hwaroak.R
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.mypage.access.MemberViewModel
import com.example.hwaroak.api.mypage.access.MemberViewModelFactory
import com.example.hwaroak.api.mypage.model.EditProfileResponse
import com.example.hwaroak.api.mypage.repository.MemberRepository
import com.example.hwaroak.data.MypageData
import com.example.hwaroak.databinding.DialogChangeNicknameBinding
import com.example.hwaroak.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {

    // 뷰 바인딩
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    //엑세스 토큰
    //유저 정보를 담을 sharedPreference
    private lateinit var pref: SharedPreferences
    private lateinit var accessToken: String

    private val memberViewModel: MemberViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = requireContext().getSharedPreferences("user", MODE_PRIVATE)
        accessToken = pref.getString("accessToken", "").toString()

        memberViewModel.getMemberInfo(accessToken)

        memberViewModel.memberInfoResult.observe(viewLifecycleOwner) {result ->
            result.onSuccess { data ->
                Log.d("member", "불러오기 성공")
                Log.d("member", "닉네임=${data.nickname}")
                Log.d("member", "자기소개=${data.introduction}")
                binding.nickname.setText(data.nickname)
                binding.etIntroduce.setText(data.introduction ?: "")
            }

            result.onFailure {
                Log.e("member", "불러오기 실패: ${it.message}")
                Toast.makeText(requireContext(), "회원정보 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }


        // 저장 버튼 리스너
        binding.btnSave.setOnClickListener {
            parentFragmentManager.popBackStack()
            val nickname = binding.nickname.text.toString().trim()
            val introduction = binding.etIntroduce.text.toString().trim()
            val profileImgUrl = "" // 추후 이미지 업로드 기능과 연결 가능



            // 🔹 observer 정의
            val resultObserver = object : Observer<Result<EditProfileResponse>> {
                override fun onChanged(result: Result<EditProfileResponse>) {
                    result.onSuccess {
                        Toast.makeText(requireContext(), "프로필 수정 완료", Toast.LENGTH_SHORT).show()
                    }

                    result.onFailure {
                        Toast.makeText(requireContext(), "프로필 수정 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                    }

                    // 🔹 observe 해제
                    memberViewModel.editProfileResult.removeObserver(this)
                }
            }

            // 🔹 버튼 클릭 시에만 observe 시작
            memberViewModel.editProfileResult.observe(viewLifecycleOwner, resultObserver)

            // 🔹 수정 요청 실행
            memberViewModel.editProfile(accessToken, nickname, profileImgUrl, introduction)
        }

        // 닉네임 변경 연필 버튼 리스너
        binding.btnEditNickname.setOnClickListener {
            showChangeNicknameDialog() // 다이얼로그 표시 함수 호출
        }
    }

    private fun showChangeNicknameDialog() {
        binding.btnEditNickname.setOnClickListener {

            // 1. 뷰 생성
            val dialogBinding = DialogChangeNicknameBinding.inflate(LayoutInflater.from(requireContext()))

            // 2. 현재 닉네임 가져와서 EditText에 적어놓기
            val currentNickname = binding.nickname.text.toString() // EditProfileFragment의 닉네임 가져오기
            dialogBinding.dialogNicknameEt.setText(currentNickname)
            dialogBinding.dialogNicknameEt.setSelection(currentNickname.length) // EditText에서 커서 맨 뒤로 가게 하기


            // 3. AlertDialog 생성
            val dialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
                .setView(dialogBinding.root) // 뷰 바인딩의 root 뷰를 설정
                .create()

            // 4. 변경 버튼 리스너와 취소 버튼 리스너
            dialogBinding.dialogChangeBtn.setOnClickListener {
                val newNickname = dialogBinding.dialogNicknameEt.text.toString()

                // EditProfileFragment의 닉네임 TextView 업데이트
                binding.nickname.text = newNickname

                dialog.dismiss() // 다이얼로그 닫기
            }

            dialogBinding.dialogCancelBtn.setOnClickListener {
                dialog.dismiss() // 다이얼로그 닫기
            }

            // 5. 다이얼로그 표시
            dialog.show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}