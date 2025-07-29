package com.example.hwaroak.api.notice.access

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hwaroak.api.notice.model.AlarmListResponse
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
            //val res = repository.
        }
    }


}