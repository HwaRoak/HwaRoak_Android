package com.example.hwaroak.api.notice.access

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hwaroak.api.notice.model.AlarmListResponse
import com.example.hwaroak.api.notice.model.AlarmSettingRequest
import com.example.hwaroak.api.notice.model.AlarmSettingResponse
import com.example.hwaroak.api.notice.model.NoticeDetailResponse
import com.example.hwaroak.api.notice.model.NoticeListResponse
import com.example.hwaroak.api.notice.repository.NoticeRepository
import kotlinx.coroutines.launch

class NoticeViewModel(private val repository: NoticeRepository) : ViewModel() {

    //공지 데이터 IN
    private val _noticeList = MutableLiveData<Result<List<NoticeListResponse>>>()
    val noticeList : MutableLiveData<Result<List<NoticeListResponse>>> = _noticeList
    private val _detailNotice = MutableLiveData<Result<NoticeDetailResponse>>()
    val detailNotice : MutableLiveData<Result<NoticeDetailResponse>> = _detailNotice

    //알람 데이터 IN
    private val _alarmList = MutableLiveData<Result<List<AlarmListResponse>>>()
    val alarmList : MutableLiveData<Result<List<AlarmListResponse>>> = _alarmList

    //알람 설정 데이터 IN
    private val _alarmSetting = MutableLiveData<Result<AlarmSettingResponse>>()
    val alarmSetting : MutableLiveData<Result<AlarmSettingResponse>> = _alarmSetting

    //1. 공지 리스트 가져오기
    fun getNoticeList(token: String){
        viewModelScope.launch {
            val res = repository.getNoticeList(token)
            _noticeList.postValue(res)
        }
    }

    //2. 알람 리스트 가져오기
    fun getAlarmList(token: String){
        viewModelScope.launch {
            val res = repository.getAlarmList(token)
            _alarmList.postValue(res)
        }
    }

    //5. 공지 상세 조회
    fun getDetailNotice(token: String, noticeId: Int){
        viewModelScope.launch {
            val res = repository.getDetailNotice(token, noticeId)
            _detailNotice.postValue(res)
        }
    }

    //5.5 차라리 공지 값을 그대로 가져오기
    suspend fun getDetailNoticeAsync(token: String, noticeId: Int)
    : NoticeDetailResponse? {
        //getOrNull() -> result에서 성공 시 값 실패 시 null
        return repository.getDetailNotice(token, noticeId).getOrNull()
    }

    //6. 알람 가져오기
    fun getAlarmSetting(token: String){
        viewModelScope.launch {
            Log.d("log_setting", "getAlarmSetting API 호출")
            val res = repository.getAlarmSetting(token)
            _alarmSetting.postValue(res)
        }
    }

    //7. 알람 세팅하기
    fun setAlarmSetting(token: String, req: AlarmSettingRequest){
        viewModelScope.launch {
            Log.d("log_setting", "setAlarmSetting API 호출")
            repository.setAlarmSetting(token, req)
        }

    }


}