package com.ls.m.ls_m_v1.approval.service

import com.ls.m.ls_m_v1.calendar.service.CalendarService
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Streaming

interface ApprovalService  {
    //  서버 통신
    @GET("path/to/your/pdf/{id}")
    @Streaming
    suspend fun downloadPDF(@Path("id") id: Int): Response<ResponseBody>

    @Multipart
    @POST("주소")
    fun uploadPdf(
        @Part pdf: MultipartBody.Part,
        @Part("approvalDTO") approvalEntity: RequestBody
    ): Call<ResponseBody>
}

object RetrofitInstanceApproval {
    private const val BASE_URL = "http://10.0.2.2:9000/api/v1"

    val instance: ApprovalService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApprovalService::class.java)
    }
}