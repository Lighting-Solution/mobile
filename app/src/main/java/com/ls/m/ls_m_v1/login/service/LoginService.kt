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
    // 보안서버로 통신
    @POST("login")
    suspend fun requestLoginData(
        @Body loginEntity: LoginEntity
    ) : Call<LoginResponseDto>
}

object RetrofitInstanceLogin{
    private const val BASE_URL = "http://10.0.2.2:9001/api/v1/lighting_solutions/security/"

    val api: LoginService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LoginService::class.java)
    }


}