package com.ls.m.ls_m_v1.emp.service

import com.ls.m.ls_m_v1.emp.entity.EmpAndroidDTO
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface EMPService {
    @GET("lite/all-emp/android")
    fun getEmpData(): Call<EmpAndroidDTO>
}

object RetrofitInstanceEMP{
    private val BASE_URL = "http://10.0.2.2:9000/api/v1/intranet/contact/"

    val api : EMPService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EMPService::class.java)
    }
}
