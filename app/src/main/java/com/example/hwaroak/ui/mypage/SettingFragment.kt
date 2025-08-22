package com.example.hwaroak.ui.mypage

import android.app.TimePickerDialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import com.example.hwaroak.R
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.notice.access.NoticeViewModel
import com.example.hwaroak.api.notice.access.NoticeViewModelFactory
import com.example.hwaroak.api.notice.model.AlarmSettingRequest
import com.example.hwaroak.api.notice.repository.NoticeRepository
import com.example.hwaroak.databinding.FragmentSettingBinding
import com.example.hwaroak.ui.main.MainActivity
import java.util.Calendar
import kotlin.getValue

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //세팅 정보를 저장할 sharedPrefernce
    private lateinit var settingPref : SharedPreferences
    private var isRemind : Boolean = true
    private var isFireAlarm : Boolean = true
    private var isOffAlarm : Boolean = false
    private var hour : Int = 0
    private var minute : Int = 0

    // 1) Retrofit 서비스로 Repository 인스턴스 생성
    private val noticeRepository by lazy {
        NoticeRepository(HwaRoakClient.noticeService)
    }
    // 2) Activity 스코프로 ViewModel 생성 (Factory 주입)
    private val noticeViewModel: NoticeViewModel by viewModels{
        NoticeViewModelFactory(noticeRepository)
    }

    //엑세스 토큰 받기
    private lateinit var pref: SharedPreferences
    private lateinit var accessToken: String


    private lateinit var binding: FragmentSettingBinding

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
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.setTopBar("알람 설정", isBackVisible = true, false)
        
        //초기 설정
        pref = requireContext().getSharedPreferences("user", MODE_PRIVATE)
        accessToken = pref.getString("accessToken", "").toString()

        //sharedPreference 초기화
        /**
         * sharedPreference : setting 사용
         * 내부 인자
         * isRemind : 리마인더 받기 여부 (Boolean)
         * isFireAlarm : 불 키우기 알람 받기 여부 (Boolean)
         * isOffAlarm : 모든 알람 끄기 여부 (Boolean)
         * hour : 리마인더 시간 (Int)
         * minute : 리마인더 분 (Int)
         * **/
        settingPref = requireActivity().getSharedPreferences("setting", 0)
        isRemind = settingPref.getBoolean("isRemind", true)
        isFireAlarm = settingPref.getBoolean("isFireAlarm", true)
        isOffAlarm = settingPref.getBoolean("isOffAlarm", false)
        hour = settingPref.getInt("hour", 0)
        minute = settingPref.getInt("minute", 0)

        //초기 설정
        binding.settingGetReminderSwitch.isChecked = isRemind
        binding.settingFireAlarmSwitch.isChecked = isFireAlarm
        binding.settingOffAlarmSwitch.isChecked = isOffAlarm
        binding.settingTimeTv.text = String.format("%02d:%02d", hour, minute)

        /**observer 설정 : API 가져오기**/
        noticeViewModel.alarmSetting.observe(viewLifecycleOwner) { result ->
            Log.d("log_setting", "알람 설정 가져오기")
            result.onSuccess { resp ->
                Log.d("log_setting", "알람 설정 가져오기 성공")
                //각각 리마인더 여부, 불 키우기 여부, 모든 알람 끄기 여부, 시간
                val isRemind2 = resp.reminderEnabled
                val isFireAlarm2 = resp.fireAlarmEnabled
                val isOffAlarm2 = resp.allOffEnabled
                val part = resp.reminderTime.split(":")
                val hour2 = part[0].toInt()
                val minute2 = part[1].toInt()
                val time2 = String.format("%02d:%02d", hour2, minute2)

                settingPref.edit {putBoolean("isRemind", isRemind2)
                    .putBoolean("isFireAlarm", isFireAlarm2)
                    .putBoolean("isOffAlarm", isOffAlarm2)
                    .putInt("hour", hour2)
                        .putInt("minute", minute2)
                        .apply()
                }
                Log.d("log_setting", "isRemind2 : $isRemind2, isFireAlarm2 : $isFireAlarm2, isOffAlarm2 : $isOffAlarm2, hour2 : $hour2, minute2 : $minute2")


                //설정
                binding.settingGetReminderSwitch.isChecked = isRemind2
                binding.settingFireAlarmSwitch.isChecked = isFireAlarm2
                binding.settingOffAlarmSwitch.isChecked = isOffAlarm2
                binding.settingTimeTv.text = time2
                updateUISetting()

            }.onFailure { err ->
                //일단 shared
                Log.d("log_setting", "알람 설정 가져오기 실패")
            }

        }
        /**1. 일단 shared로 1차 설정**/
        //UI 세팅
        updateUISetting()
        //버튼 setting
        settingListener()

        /**2. API로 2차 설정**/
        noticeViewModel.getAlarmSetting(accessToken)




    }
    
    
    //각 스위치를 눌렀을 때 로직 구성 함수
    private fun updateUISetting(){
        sendToAPI()
        val disableAll = binding.settingOffAlarmSwitch.isChecked
        val remind = binding.settingGetReminderSwitch.isChecked
        val fire = binding.settingFireAlarmSwitch.isChecked

        settingPref.edit { putBoolean("isRemind", remind).
                            putBoolean("isFireAlarm", fire).
                            putBoolean("isOffAlarm", disableAll).apply() }

        //스위치는 버튼 활성화 된 상태에만 OK
        var color = ContextCompat.getColor(requireContext(), R.color.colorGrayIcon)
        if(binding.settingGetReminderSwitch.isChecked && !disableAll){
            binding.settingReminderTimeBtn.isEnabled = true
            color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        }
        else{
            binding.settingReminderTimeBtn.isEnabled = false
            color = ContextCompat.getColor(requireContext(), R.color.colorGrayIcon)
        }
        binding.settingReminderTimeBtn.backgroundTintList = ColorStateList.valueOf(color)

    }

    //각 버튼 리스너 등록
    private fun settingListener(){
        //리마인더 받기
        binding.settingGetReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){binding.settingOffAlarmSwitch.isChecked = false}
            //settingPref.edit { putBoolean("isRemind", isChecked).apply() }

            updateUISetting()
        }
        //불 키우기
        binding.settingFireAlarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){binding.settingOffAlarmSwitch.isChecked = false}
            updateUISetting()
        }
        //모두 끄기
        binding.settingOffAlarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 켜지면 다른 스위치 꺼버리기
                binding.settingGetReminderSwitch.isChecked = false
                binding.settingFireAlarmSwitch.isChecked  = false
            }
            updateUISetting()

        }
        //시간 설정 버튼
        binding.settingReminderTimeBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val tp = TimePickerDialog(
                requireContext(),
                { _, h, m ->
                    //선택한 시간 포맷 & 저장
                    binding.settingTimeTv.text = String.format("%02d:%02d", h, m)
                    settingPref.edit {
                        putInt("hour", h)
                        .putInt("minute", m)
                        .apply()
                    }
                    sendToAPI()
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true //24h 형식(00-23시)
            )
            tp.show()
        }
    }


    //화면이 바뀔 때 보내기
    override fun onPause() {
        super.onPause()

        //sendToAPI()

    }

    private fun sendToAPI() {
        Log.d("log_setting", "sendToAPI 실행 - 저장할게요")
        isRemind = settingPref.getBoolean("isRemind", true)
        isFireAlarm = settingPref.getBoolean("isFireAlarm", true)
        isOffAlarm = settingPref.getBoolean("isOffAlarm", false)
        hour = settingPref.getInt("hour", 0)
        minute = settingPref.getInt("minute", 0)
        Log.d("log_setting", "isRemind : $isRemind, isFireAlarm : $isFireAlarm, isOffAlarm : $isOffAlarm, hour : $hour, minute : $minute")
        val req  = AlarmSettingRequest(
            isRemind,
            String.format("%02d:%02d:00", hour, minute),
            isFireAlarm,
            isOffAlarm
        )
        noticeViewModel.setAlarmSetting(accessToken, req)
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}