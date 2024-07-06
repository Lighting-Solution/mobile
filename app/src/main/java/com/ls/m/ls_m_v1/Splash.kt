package com.ls.m.ls_m_v1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.emp.entity.EmpAndroidDTO
import com.ls.m.ls_m_v1.emp.service.EMPService
import com.ls.m.ls_m_v1.login.Login
import retrofit2.Call
import retrofit2.Response

class Splash : AppCompatActivity() {
    private lateinit var context : Context
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var empService: EMPService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        // 여기서 로그인 토큰 확인하고
        // 로그인화면으로 갈지, 메인화면으로 갈지 결정

        // 데이터베이스 생성
        dbHelper= DatabaseHelper(this)
        dbHelper.writableDatabase

        // emp data 읽어오기
        empService.getEmpData().enqueue(object : retrofit2.Callback<EmpAndroidDTO>{
            override fun onResponse(call: Call<EmpAndroidDTO>, response: Response<EmpAndroidDTO>) {

                if (response.isSuccessful){
                    //성공적으로 업데이트.
                    Toast.makeText(context, "업데이트 성공", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@Splash, Login::class.java)
                        startActivity(intent)
                        finish()
                    }, 2000)
                }else{
                    Toast.makeText(context, "업데이트 실패, 다시 시도해 주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EmpAndroidDTO>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@Splash, Login::class.java)
            startActivity(intent)
            finish()
        }, 2000)

    }
}