package com.example.hwaroak.ui.mypage

import android.R.attr.text
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
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
import com.example.hwaroak.ui.main.MainActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

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

    private val getContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { handleSelectedImage(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**상단바 수정**/
        (activity as? MainActivity)?.setTopBar("프로필 수정", isBackVisible = true, false)

        pref = requireContext().getSharedPreferences("user", MODE_PRIVATE)
        accessToken = pref.getString("accessToken", "").toString()

        memberViewModel.getMemberInfo(accessToken)

        memberViewModel.memberInfoResult.observe(viewLifecycleOwner) {result ->
            result.onSuccess { data ->
                Log.d("member", "불러오기 성공")
                Log.d("member", "닉네임=${data.nickname}")
                Log.d("member", "자기소개=${data.introduction}")
                binding.editProfileNicknameTv.setText(data.nickname)
                binding.editProfileUserIdTv.setText(data.userId)
                binding.editProfileIntroductionEt.setText(data.introduction ?: "")
            }

            result.onFailure {
                Log.e("member", "불러오기 실패: ${it.message}")
                Toast.makeText(requireContext(), "회원정보 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }

        memberViewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                // 삭제 성공 → 기본 이미지로 변경
                Glide.with(this)
                    .load(R.drawable.default_profile_image) // 기본 이미지
                    .into(binding.editProfileImageIv)

                Toast.makeText(requireContext(), "프로필 이미지가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }.onFailure { e ->
                Toast.makeText(requireContext(), e.message ?: "삭제 실패", Toast.LENGTH_SHORT).show()
            }
        }
        // 바텀시트다이얼로그 띄우기 (로직은 밑에 있음)
        binding.editProfileImageBtn.setOnClickListener {
            showChangeImageDialog()
        }

        binding.editProfileCopyIdBtn.setOnClickListener {
            val userId = binding.editProfileUserIdTv.text.toString()
            copyUserIdToClipboard(userId)
        }

        // 저장 버튼 리스너
        binding.editProfileSaveBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
            val nickname = binding.editProfileNicknameTv.text.toString().trim()
            val introduction = binding.editProfileIntroductionEt.text.toString().trim()
            val profileImgUrl = "" // 추후 이미지 업로드 기능과 연결 가능

            // 수정한 닉네임 캐시에 즉시 저장
            val pref = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
            pref.edit().putString("cachedNickname", nickname).apply()

            //상단 바 이름에도 적용
            (activity as? MainActivity)?.changeTitle(nickname)

            // 수정 요청 실행
            memberViewModel.editProfile(accessToken, nickname, introduction)

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
        binding.editProfileNicknameBtn.setOnClickListener {
            showChangeNicknameDialog() // 다이얼로그 표시 함수 호출
        }

        memberViewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { confirm ->
                // 서버가 내려준 최종 URL
                val url = confirm.profileImageUrl
                // 새 이미지 표시
                Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.default_profile_image) // 프로젝트 리소스로 교체
                    .error(R.drawable.default_profile_image)
                    .into(binding.editProfileImageIv)

                Toast.makeText(requireContext(), "프로필 사진이 변경되었어요.", Toast.LENGTH_SHORT).show()
            }.onFailure { e ->
                Toast.makeText(requireContext(), e.message ?: "업로드 실패", Toast.LENGTH_SHORT).show()
            }
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
            val currentNickname = binding.editProfileNicknameTv.text.toString() // EditProfileFragment의 닉네임 가져오기
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
                binding.editProfileNicknameTv.text = newNickname
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
            sheetBinding.dialogMenuDivider.visibility = View.GONE
            sheetBinding.dialogDefaultImageTv.visibility = View.GONE
        }

        sheetBinding.dialogChooseImageTv.setOnClickListener {
            // 앨범에서 사진 선택
            getContent.launch("image/*")
            dialog.dismiss()
        }

        sheetBinding.dialogDefaultImageTv.setOnClickListener {
            // 프로필 이미지 삭제 후 기본 이미지로 변경 로직
            val token = obtainAuthTokenOrThrow()
            memberViewModel.deleteProfileImage(token)
            dialog.dismiss()
        }

        sheetBinding.dialogCancelTv.setOnClickListener {
            // 취소 버튼 로직
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun handleSelectedImage(uri: Uri) {
        val cr = requireContext().contentResolver

        // MIME
        val mime = cr.getType(uri) ?: "application/octet-stream"

        // 원본 표시명 (없을 수 있음)
        val originalName: String? = runCatching {
            cr.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
                if (c.moveToFirst()) c.getString(0) else null
            }
        }.getOrNull()

        // 확장자 결정
        val extFromOriginal = originalName
            ?.substringAfterLast('.', missingDelimiterValue = "")
            ?.takeIf { it.isNotBlank() }

        val extFromMime = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)

        val finalExt = extFromOriginal ?: extFromMime ?: "jpg"

        // 베이스 파일명
        val baseName = when {
            !originalName.isNullOrBlank() -> originalName.substringBeforeLast('.', originalName)
            else -> "profile_${System.currentTimeMillis()}"
        }

        val finalFileName = "$baseName.$finalExt"

        // 토큰 가져오기 (프로젝트 방식에 맞게 교체)
        val token = obtainAuthTokenOrThrow()

        Log.d("log_my", "$token | $mime | $finalFileName | $uri")

        // ViewModel 호출 (서버: Presign → S3 PUT → Confirm)
        memberViewModel.uploadProfileImage(
            token = token,
            contentType = mime,
            fileName = finalFileName,
            uri = uri,
            context = requireContext()
        )
    }

    private fun obtainAuthTokenOrThrow(): String {
        pref = requireContext().getSharedPreferences("user", MODE_PRIVATE)
        val token = pref.getString("accessToken", "").toString()
        return token ?: throw IllegalStateException("인증 토큰이 없습니다.")
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