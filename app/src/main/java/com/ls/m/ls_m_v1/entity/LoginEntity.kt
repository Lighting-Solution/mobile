package com.ls.m.ls_m_v1.entity

import com.google.gson.annotations.SerializedName

data class LoginEntity(
    @SerializedName("account_id") val id : String,
    @SerializedName("account_pw") val pw : String
)

data class ResponseEntity(
    var data : String
)