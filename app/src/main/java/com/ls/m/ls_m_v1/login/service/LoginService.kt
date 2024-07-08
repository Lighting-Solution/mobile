package com.ls.m.ls_m_v1.login.service

import com.ls.m.ls_m_v1.login.entity.LoginEntity
import com.ls.m.ls_m_v1.login.entity.LoginResponseDto
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    // 서버 통신 하는곳
    @POST("Login/data")
    suspend fun requestLoginData(
        @Body loginEntity: LoginEntity
    ) : Call<LoginResponseDto>// 받아올 데이터 형식 확인 후 수정할 것
}

object RetrofitInstanceLogin{
    private const val BASE_URL = "http://10.0.2.2:9000/api/v1/emp/"

    val api: LoginService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LoginService::class.java)
    }


}