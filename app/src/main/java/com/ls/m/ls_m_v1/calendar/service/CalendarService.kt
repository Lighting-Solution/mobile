package com.ls.m.ls_m_v1.calendar.service

import com.ls.m.ls_m_v1.calendar.entity.CalendarDto
import com.ls.m.ls_m_v1.calendar.entity.CalendarEntity
import com.ls.m.ls_m_v1.calendar.entity.UpdateCalendarDto
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CalendarService {
    // 서버 통신하는 곳
    @GET("calendar/data")
    suspend fun requestCalendarData(
        @Header("token") token :String,
        @Body empId : Int
    ): List<CalendarEntity>

    @POST("크리에이트 주소")
    fun createCalendarData(
        @Header("token") token: String,
        @Body calendarDto : CalendarDto
    ): Call<String>

    @PUT("주소")
    fun updataCalendarData(
    // 업데이트
        @Header("token") token: String,
        @Body updateCalendarDto: UpdateCalendarDto
    ):Call<String>

    @DELETE("주소")
    fun deleteCalendarData(
        //아마 토큰이랑 데이터 아이디
        @Header("token") token: String,
        @Body calendarId : Int
    ): Call<String>

}

object RetrofitInstanceCalender {
    private const val BASE_URL = "http://localhost:9000/api/v1/lighting_solutions/calendar/events"

    val api: CalendarService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CalendarService::class.java)
    }
}