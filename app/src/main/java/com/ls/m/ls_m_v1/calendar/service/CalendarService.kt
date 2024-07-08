package com.ls.m.ls_m_v1.calendar.service

import com.ls.m.ls_m_v1.calendar.entity.CalendarEntity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CalendarService {
    // 서버 통신하는 곳
    @GET("calendar/data")
    suspend fun requestCalendarData(
//        @Header("token") token :String,
        @Body empId : Int
    ): List<CalendarEntity>
}

object RetrofitInstanceCalender {
    private const val BASE_URL = "http://10.0.2.2:9000/api/v1/"

    val api: CalendarService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CalendarService::class.java)
    }
}