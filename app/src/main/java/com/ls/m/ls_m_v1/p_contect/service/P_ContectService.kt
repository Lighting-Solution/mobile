package com.ls.m.ls_m_v1.p_contect.service

import com.ls.m.ls_m_v1.p_contect.dto.AddPersonalDTO
import com.ls.m.ls_m_v1.p_contect.entity.ContanctAandroidDTO
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface P_ContectService {
    @GET("list/all-personal/android/{id}")
    fun getP_ContectData(
        @Path("id") id:String
    ): Call<ContanctAandroidDTO>

    @DELETE("contact")
    fun deleteP_ContectData(
        @Body deleteId : String
    ):Call<String>

    @PUT("contact/{id}")
    fun updateP_ContectData(
        @Path("id") id: String,
        @Body addPersonalDTO: AddPersonalDTO
    ): Call<String>

    // insert
    @POST("contact")
    fun addPersnalData(
        @Body addPersonalDTO: AddPersonalDTO
    ): Call<String>
}
object RetrofitInstancePersonal {
    private val BASE_URL = "http://10.0.2.2:9000/api/v1/intranet/contact/"

    val api : P_ContectService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(P_ContectService::class.java)
    }
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

//참고
/*
*  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        fetchAndStoreData()
    }

    private fun fetchAndStoreData() {
        val service = RetrofitClient.instance.create(P_ContectService::class.java)
        service.getP_ContectData().enqueue(object : Callback<ContanctAandroidDTO> {
            override fun onResponse(call: Call<ContanctAandroidDTO>, response: Response<ContanctAandroidDTO>) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        storeDataInDatabase(data)
                    }
                } else {
                    Log.e("MainActivity", "API 호출 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ContanctAandroidDTO>, t: Throwable) {
                Log.e("MainActivity", "API 호출 실패: ${t.message}")
            }
        })
    }

    private fun storeDataInDatabase(data: ContanctAandroidDTO) {
        data.personalContactDTOList.forEach { contact ->
            dbHelper.insertPersonalContact(contact)
        }
        // 추가적으로 EmpDTO 등 다른 데이터도 저장하려면 이곳에 코드를 추가합니다.
        Log.d("MainActivity", "데이터베이스에 데이터 저장 완료")
    }
}
* */