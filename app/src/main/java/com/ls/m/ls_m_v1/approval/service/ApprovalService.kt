package com.ls.m.ls_m_v1.approval.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ApprovalService {
    //  서버 통신
}

object RetrofitInstanceApproval {
    private const val BASE_URL = "http://10.0.2.2:9000/api/v1"

    val api: ApprovalService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApprovalService::class.java)
    }
}