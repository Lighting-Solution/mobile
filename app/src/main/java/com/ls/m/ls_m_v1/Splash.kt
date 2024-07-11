package com.ls.m.ls_m_v1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.emp.entity.EmpAndroidDTO
import com.ls.m.ls_m_v1.emp.repository.EmpRepository
import com.ls.m.ls_m_v1.emp.service.EMPService
import com.ls.m.ls_m_v1.emp.service.RetrofitInstanceEMP
import com.ls.m.ls_m_v1.login.Login
import com.ls.m.ls_m_v1.login.repository.LoginRepository
import com.ls.m.ls_m_v1.p_contect.entity.ContanctAandroidDTO
import com.ls.m.ls_m_v1.p_contect.repository.PersonalContactRepository
import com.ls.m.ls_m_v1.p_contect.service.P_ContectService
import com.ls.m.ls_m_v1.p_contect.service.RetrofitInstancePersonal
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
    private lateinit var personalContactRepository : PersonalContactRepository
private lateinit var p_contactService : P_ContectService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        // 데이터베이스 생성 및 초기화
        empRepository = EmpRepository(this)
        dbHelper = DatabaseHelper(this)
        personalContactRepository = PersonalContactRepository(this)
//        val db = dbHelper.writableDatabase

//        dbHelper.clearDatabase()
        // 데이터 베이스 초기화
        // 로그인 데이터 확인 및 화면 전환
        handleLoginData()


        //// 작업 다하면 지울것-------------------
//        Handler(Looper.getMainLooper()).postDelayed({
//            val intent = Intent(this@Splash, Login::class.java)
//            startActivity(intent)
//            finish()
//        }, 1000)
    }

    private fun updateEmpData(token: String) {
        empService = RetrofitInstanceEMP.api
        empService.getEmpData("Bearer $token").enqueue(object : Callback<EmpAndroidDTO> {
            override fun onResponse(call: Call<EmpAndroidDTO>, response: Response<EmpAndroidDTO>) {
                if (response.isSuccessful) {
                    Log.d("UpdateEmpData", "Response successful")
                    val empAndroidDTO = response.body()
                    val empDtoList = empAndroidDTO?.empDTOList
                    val positionList = empAndroidDTO?.positionDTOList
                    val departmentList = empAndroidDTO?.departmentDTOList

                    if (empDtoList != null) {
                        empRepository.insertEmp(empDtoList)
                        if (positionList != null) {
                            empRepository.insertPosition(positionList)
                        }
                        if (departmentList != null) {
                            empRepository.insertDepartment(departmentList)
                        }
                        Toast.makeText(this@Splash, "업데이트 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("UpdateEmpData", "empDtoList is null")
                        Toast.makeText(this@Splash, "업데이트 실패, 데이터가 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Log.e(
                        "UpdateEmpData",
                        "Response not successful: ${response.errorBody()?.string()}"
                    )
                    Toast.makeText(this@Splash, "업데이트 실패, 다시 시도해 주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EmpAndroidDTO>, t: Throwable) {
                Log.e("UpdateEmpData", "Request failed: ${t.message}")
                Toast.makeText(this@Splash, "업데이트 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleLoginData() {
        loginRepository = LoginRepository(this)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val user = loginRepository.getloginData()

                // EMP 데이터 업데이트
//                empRepository.forRefreash()
//                updateEmpData(user.token)
                updatePersonal(user.empId, user.token)
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

    private fun updatePersonal(id: Int, token: String) {
        p_contactService = RetrofitInstancePersonal.api
        // 데이터베이스에 데이터가 있는지 확인
        val personalContactCount = personalContactRepository.getPersonalContactCount()
        if (personalContactCount > 0) {
            // 데이터가 이미 존재하면 업데이트하지 않음
            return
        }
        p_contactService.getP_ContectData(token, id)
            .enqueue(object : Callback<ContanctAandroidDTO> {
                override fun onResponse(
                    call: Call<ContanctAandroidDTO>,
                    response: Response<ContanctAandroidDTO>
                ) {
                    if (response.isSuccessful) {
                        val personalData = response.body()
                        // 개인 주소록 데이터를 처리합니다.
                        personalData?.let {
                            // Company 데이터를 먼저 삽입
                            it.personalContactDTOList.forEach { contact ->
                                personalContactRepository.insertCompany(contact.company)
                            }
                            // PersonalContact 데이터를 삽입
                            it.personalContactDTOList.forEach { contact ->
                                personalContactRepository.insertPersonalContact(contact)
                            }
                            // PersonalGroup 데이터를 삽입
                            it.personalGroupDTOList.forEach { group ->
                                personalContactRepository.insertPersonalGroup(group)
                            }
                            // ContactGroup 데이터를 삽입
                            it.contactGroupDTOList.forEach { contactGroup ->
                                personalContactRepository.insertContactGroup(contactGroup)
                            }

                            Toast.makeText(this@Splash, "데이터 삽입 완료", Toast.LENGTH_SHORT)
                                .show()

                        }
                    } else {

                        Toast.makeText(this@Splash, "데이터 가져오기 실패", Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<ContanctAandroidDTO>, t: Throwable) {

                    Toast.makeText(this@Splash, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT)
                        .show()

                }
            })
    }
}