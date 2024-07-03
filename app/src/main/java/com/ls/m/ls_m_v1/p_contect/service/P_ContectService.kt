package com.ls.m.ls_m_v1.p_contect.service

import com.ls.m.ls_m_v1.p_contect.entity.ContanctAandroidDTO
import retrofit2.Call
import retrofit2.http.GET

interface P_ContectService {
    @GET("list/all-personal/android")
    fun getP_ContectData(): Call<ContanctAandroidDTO>
}
// 참고
/*
* apiService.getEmpAndroidData().enqueue(object : Callback<EmpAndroidDTO> {
            override fun onResponse(call: Call<EmpAndroidDTO>, response: Response<EmpAndroidDTO>) {
                if (response.isSuccessful) {
                    val empAndroidData = response.body()
                    // 데이터 사용
                    Toast.makeText(this@MainActivity, "Data fetched successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EmpAndroidDTO>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
* */