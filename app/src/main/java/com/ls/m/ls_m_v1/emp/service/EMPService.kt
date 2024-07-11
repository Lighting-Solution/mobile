package com.ls.m.ls_m_v1.emp.service

import com.ls.m.ls_m_v1.emp.entity.EmpAndroidDTO
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

interface EMPService {
    @GET("list/all-emp/android")
    fun getEmpData(
        @Header("Authorization") token:String
    ): Call<EmpAndroidDTO>
}

object RetrofitInstanceEMP{
    private val BASE_URL = "http://10.0.2.2:9002/api/v1/lighting_solutions/security/contact/"

    val httpClient = OkHttpClient.Builder()

    val api : EMPService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
            .create(EMPService::class.java)
    }
}
