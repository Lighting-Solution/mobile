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
    private const val BASE_URL = "http://10.0.2.2:9000/api/v1/intranet/contact/"

    val api : EMPService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EMPService::class.java)
    }
}

// 데이터 받아와서 넣는거까지 해보자
/*
* 1. 어디서 요청을 보낼것인가.
* -> 로그인 할때?
* -> 조직도 페이지 들어갈때?
* -> 회사의 테이블은 무조건 들어가있고,  1번 데이터는 필수로 저장하고
* 가져 올때는 1번을 제외하고 저장할것.****
*
* 데이터는 슬렉에 참고 하고,
* api가 가장 일이다.. 너무 항목이 많아..
*
* DELETE FROM table_name WHERE empId != 1;
* 1빼고 삭제하는 쿼리문
* 1을 빼고 저장하는 조건 문
* private fun storeDataInDatabase(data: ContanctAandroidDTO) {
        val filteredContacts = data.personalContactDTOList.filter { it.personalContactId != 1 }
        filteredContacts.forEach { contact ->
            dbHelper.insertPersonalContact(contact)
        }
* */