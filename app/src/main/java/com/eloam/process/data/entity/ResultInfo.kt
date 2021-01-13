package com.eloam.process.data.entity

data class ResultInfo(
    val code: Int,
    val `data`: Any,
    val msg: String,
    val success: Boolean
)