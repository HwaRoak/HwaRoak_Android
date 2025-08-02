package com.example.hwaroak.ui.mypage

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.hwaroak.R
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.mypage.access.MemberViewModel
import com.example.hwaroak.api.mypage.access.MemberViewModelFactory
import com.example.hwaroak.api.mypage.model.EditProfileResponse
import com.example.hwaroak.api.mypage.repository.MemberRepository
import com.example.hwaroak.data.MypageData
import com.example.hwaroak.databinding.DialogChangeImageBinding
import com.example.hwaroak.databinding.DialogChangeNicknameBinding
import com.example.hwaroak.databinding.FragmentEditProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class EditProfileFragment : Fragment() {

    // 뷰 바인딩
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    //엑세스 토큰
    //유저 정보를 담을 sharedPreference
    private lateinit var pref: SharedPreferences
    private lateinit var accessToken: String

    private var profileImgUrl: String? = null

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
                binding.userId.setText(data.userId)
                binding.etIntroduce.setText(data.introduction ?: "")
            }

            result.onFailure {
                Log.e("member", "불러오기 실패: ${it.message}")
                Toast.makeText(requireContext(), "회원정보 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }

        // 바텀시트다이얼로그 띄우기 (로직은 밑에 있음)
        binding.btnChangeProfileImage.setOnClickListener {
            showChangeImageDialog()
        }

        binding.btnCopyId.setOnClickListener {
            val userId = binding.userId.text.toString()
            copyUserIdToClipboard(userId)
        }

        // 저장 버튼 리스너
        binding.btnSave.setOnClickListener {
            parentFragmentManager.popBackStack()
            val nickname = binding.nickname.text.toString().trim()
            val introduction = binding.etIntroduce.text.toString().trim()
            val profileImgUrl = "" // 추후 이미지 업로드 기능과 연결 가능

            // 수정한 닉네임 캐시에 즉시 저장
            val pref = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
            pref.edit().putString("cachedNickname", nickname).apply()

            // 수정 요청 실행
            memberViewModel.editProfile(accessToken, nickname, profileImgUrl, introduction)

            // observer 정의
            val resultObserver = object : Observer<Result<EditProfileResponse>> {
                override fun onChanged(result: Result<EditProfileResponse>) {
                    result.onSuccess {
                        Toast.makeText(requireContext(), "프로필 수정 완료", Toast.LENGTH_SHORT).show()
                    }

                    result.onFailure {
                        Toast.makeText(requireContext(), "프로필 수정 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                    }

                    // observe 해제
                    memberViewModel.editProfileResult.removeObserver(this)
                }
            }

            // 버튼 클릭 시에만 observe 시작
            memberViewModel.editProfileResult.observe(viewLifecycleOwner, resultObserver)

        }

        // 닉네임 변경 연필 버튼 리스너
        binding.btnEditNickname.setOnClickListener {
            showChangeNicknameDialog() // 다이얼로그 표시 함수 호출
        }
    }

    private fun copyUserIdToClipboard(userId: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("userId", userId)
        clipboard.setPrimaryClip(clip)

        // 안드로이드 OS 자체에서 "클립보드에 복사했어요" 토스트 메시지 출력됨
        // Toast.makeText(requireContext(), "아이디가 복사되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun showChangeNicknameDialog() {
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

            // 텍스트 변화 감지해서 버튼 활성/비활성
            dialogBinding.dialogNicknameEt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val input = s.toString().trim()
                    if (input.isEmpty() || input == currentNickname) {
                        dialogBinding.dialogChangeBtn.setBackgroundResource(R.drawable.bg_diary_write_no_btn)
                        dialogBinding.dialogChangeBtn.isEnabled = false
                    } else {
                        dialogBinding.dialogChangeBtn.setBackgroundResource(R.drawable.bg_diary_write_btn)
                        dialogBinding.dialogChangeBtn.isEnabled = true
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            // 4. 변경 버튼 리스너와 취소 버튼 리스너
            dialogBinding.dialogChangeBtn.setOnClickListener {
                val newNickname = dialogBinding.dialogNicknameEt.text.toString()

                if (newNickname.isEmpty()) {
                    Toast.makeText(requireContext(), "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // EditProfileFragment의 닉네임 TextView 업데이트
                binding.nickname.text = newNickname

                dialog.dismiss() // 다이얼로그 닫기
            }

            dialogBinding.dialogCancelBtn.setOnClickListener {
                dialog.dismiss() // 다이얼로그 닫기
            }

            // 다이얼로그 처음 뜰 때도 비활성화 상태로
            dialogBinding.dialogChangeBtn.setBackgroundResource(R.drawable.bg_diary_write_no_btn)
            dialogBinding.dialogChangeBtn.isEnabled = false

            // 5. 다이얼로그 표시
            dialog.show()
    }

    private fun showChangeImageDialog() {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)

        val sheetBinding = DialogChangeImageBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        dialog.window?.setDimAmount(0.3f)

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(
                com.google.android.material.R.id.design_bottom_sheet
            ) ?: return@setOnShowListener

        }

        // 조건에 따라 '기본 이미지 적용' 메뉴 숨김
        if (isDefaultProfileImage()) {
            sheetBinding.changeDefaultImageTv.visibility = View.GONE
        }

        sheetBinding.chooseFromGalleryTv.setOnClickListener {
            // 앨범에서 사진 선택
            dialog.dismiss()
        }

        sheetBinding.changeDefaultImageTv.setOnClickListener {
            // 프로필 이미지 삭제 후 기본 이미지로 변경 로직
            profileImgUrl = null
            dialog.dismiss()
        }

        sheetBinding.cancelTv.setOnClickListener {
            // 취소 버튼 로직
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun isDefaultProfileImage(): Boolean {
        return profileImgUrl.isNullOrEmpty()
//        return profileImgUrl.isNullOrEmpty() || profileImgUrl == DEFAULT_PROFILE_IMAGE_URL
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}