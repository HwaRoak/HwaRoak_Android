package com.example.hwaroak.ui.mypage

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Selection.setSelection
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.example.hwaroak.R
import com.example.hwaroak.databinding.DialogChangeNicknameBinding
import com.example.hwaroak.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)


        // 저장 버튼 리스너
        binding.btnSave.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 닉네임 변경 연필 버튼 리스너
        binding.btnEditNickname.setOnClickListener {
            showChangeNicknameDialog() // 다이얼로그 표시 함수 호출
        }

        return binding.root
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