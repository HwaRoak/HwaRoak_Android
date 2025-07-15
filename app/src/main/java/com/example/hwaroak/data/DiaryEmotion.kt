package com.example.hwaroak.data

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.annotation.DrawableRes

//일기 쓰기 화면에서 recyclerview에서 쓸 형식
@Parcelize
data class DiaryEmotion (
    val name : String,
    @DrawableRes val image : Int
) : Parcelable