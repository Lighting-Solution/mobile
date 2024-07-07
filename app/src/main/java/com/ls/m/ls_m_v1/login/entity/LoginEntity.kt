package com.ls.m.ls_m_v1.login.entity

import com.google.gson.annotations.SerializedName
import retrofit2.Call

data class LoginEntity(
    @SerializedName("account_id") val id : String,
    @SerializedName("account_pw") val pw : String
    // 나 모바일임 하고 같이 보냄
)

data class ResponseDto(
    var token : String,
    var empId: Int
)