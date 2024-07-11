package com.ls.m.ls_m_v1.login.entity

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import java.io.Serializable

data class LoginEntity(
    @SerializedName("accountId") val id : String,
    @SerializedName("accountPw") val pw : String
    // 나 모바일임 하고 같이 보냄
)

data class LoginResponseDto(
    var token : String,
    var empId: Int,
    var positionId : Int,
    var empName : String,
    var departmentId : Int
) : Serializable


data class UserResponse(
    val token: String,
    val empId: String,
    val positionId: String,
    val empName: String,
    val departmentId: String
)