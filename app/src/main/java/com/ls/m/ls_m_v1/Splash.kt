package com.ls.m.ls_m_v1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.emp.entity.EmpAndroidDTO
import com.ls.m.ls_m_v1.emp.repository.EmpRepository
import com.ls.m.ls_m_v1.emp.service.EMPService
import com.ls.m.ls_m_v1.emp.service.RetrofitInstanceEMP
import com.ls.m.ls_m_v1.login.Login
import com.ls.m.ls_m_v1.login.repository.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Splash : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var empService: EMPService
    private lateinit var loginRepository: LoginRepository
    private lateinit var empRepository: EmpRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 데이터베이스 생성 및 초기화
        dbHelper = DatabaseHelper(this)
        dbHelper.writableDatabase

        empService = RetrofitInstanceEMP.api

        empRepository = EmpRepository(this)
        // EMP 데이터 업데이트
        updateEmpData()

        // 로그인 데이터 확인 및 화면 전환
        handleLoginData()


        //// 작업 다하면 지울것-------------------
//        Handler(Looper.getMainLooper()).postDelayed({
//            val intent = Intent(this@Splash, Login::class.java)
//            startActivity(intent)
//            finish()
//        }, 1000)
    }

    private fun updateEmpData() {
        empService.getEmpData().enqueue(object : Callback<EmpAndroidDTO> {
            override fun onResponse(call: Call<EmpAndroidDTO>, response: Response<EmpAndroidDTO>) {
                if (response.isSuccessful) {
                    // 성공적으로 업데이트
                    Toast.makeText(this@Splash, "업데이트 성공", Toast.LENGTH_SHORT).show()

                    val empDtoList = response.body()?.empDTOList
                    val positionList = response.body()?.positionDTOList
                    val departmentList = response.body()?.departmentDTOList

                    if (empDtoList != null) {
                        empRepository.insertEmp(empDtoList)
                        if (positionList != null)
                            empRepository.insertPosition(positionList)

                        if (departmentList != null)
                            empRepository.insertDepartment(departmentList)
                    } else {
                        Toast.makeText(this@Splash, "업데이트 실패 , 데이터가 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this@Splash, "업데이트 실패, 다시 시도해 주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EmpAndroidDTO>, t: Throwable) {
                Toast.makeText(this@Splash, "업데이트 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleLoginData() {
        loginRepository = LoginRepository(this)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val user = loginRepository.getloginData()

                // 로그인 데이터가 있을 경우 메인 화면으로 이동
                val intent = Intent(this@Splash, MainActivity::class.java)
                startActivity(intent)
                finish()

            } catch (e: IllegalStateException) {
                // 로그인 데이터가 없을 경우 로그인 화면으로 이동
                val intent = Intent(this@Splash, Login::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}