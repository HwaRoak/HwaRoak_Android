package com.example.hwaroak.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AgreeViewModel : ViewModel(){
    val serviceAgree = MutableLiveData(false)
    val privacyAgree = MutableLiveData(false)

    val allGo = MutableLiveData(false)
}