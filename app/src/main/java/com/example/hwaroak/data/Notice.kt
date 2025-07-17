package com.example.hwaroak.data

data class Notice(
    val title: String,
    val detail: String,
    var isExpanded: Boolean = false
)
