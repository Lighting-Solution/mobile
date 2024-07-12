package com.ls.m.ls_m_v1.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ls.m.ls_m_v1.MainActivity
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.approval.repository.ApprovalRepository
import com.ls.m.ls_m_v1.approval.service.ApprovalService
import com.ls.m.ls_m_v1.approval.service.RetrofitInstanceApproval
import com.ls.m.ls_m_v1.calendar.repository.CalendarRepository
import com.ls.m.ls_m_v1.calendar.service.CalendarService
import com.ls.m.ls_m_v1.calendar.service.RetrofitInstanceCalender
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.emp.entity.EmpAndroidDTO
import com.ls.m.ls_m_v1.emp.repository.EmpRepository
import com.ls.m.ls_m_v1.emp.service.EMPService
import com.ls.m.ls_m_v1.emp.service.RetrofitInstanceEMP
import com.ls.m.ls_m_v1.login.entity.LoginEntity
import com.ls.m.ls_m_v1.login.entity.LoginResponseDto
import com.ls.m.ls_m_v1.login.repository.LoginRepository
import com.ls.m.ls_m_v1.login.service.RetrofitInstanceLogin
import com.ls.m.ls_m_v1.p_contect.entity.ContanctAandroidDTO
import com.ls.m.ls_m_v1.p_contect.repository.PersonalContactRepository
import com.ls.m.ls_m_v1.p_contect.service.P_ContectService
import com.ls.m.ls_m_v1.p_contect.service.RetrofitInstancePersonal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class Login : AppCompatActivity() {
    private lateinit var id: EditText
    private lateinit var pw: EditText
    private lateinit var loginButton: Button

    private lateinit var dbHelper: DatabaseHelper

    private lateinit var loginRepository: LoginRepository
    private lateinit var empRepository: EmpRepository
    private lateinit var calendarRepository: CalendarRepository
    private lateinit var personalContactRepository: PersonalContactRepository

    private lateinit var empService: EMPService
    private lateinit var calendarService: CalendarService
    private lateinit var p_contactService : P_ContectService

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        id = findViewById(R.id.userID)
        pw = findViewById(R.id.userPassword)
        loginButton = findViewById(R.id.loginButton)

        // 인스턴스 생성하고 데이터베이스 액세스
        // 데이터베이스와 레포 초기화
        empRepository = EmpRepository(this)
        dbHelper = DatabaseHelper(this)
        loginRepository = LoginRepository(this)
        personalContactRepository = PersonalContactRepository(this)

        // 서비스 초기화

        loginButton.setOnClickListener {
            val userId = id.text.toString()
            val userPw = pw.text.toString()

            // api통신으로 받아와 데이터 베이스 저장까지 완료
            CoroutineScope(Dispatchers.Main).launch {
                val loginResponse = login(userId, userPw)
                if (loginResponse != null) {
                    // 로그인 성공 시 처리

                    // emp 통신
                    updateEmpData(loginResponse.token)

                    // personal contact
                    updatePersonal(loginResponse.empId, loginResponse.token)



                    val intent = Intent(this@Login, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // 로그인 실패 시 처리
                }
            }

        }


    }

    private suspend fun login(userId: String, userPw: String): LoginResponseDto? {
        loginRepository = LoginRepository(this)
        val loginEntity = LoginEntity(userId, userPw)

        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstanceLogin.api.requestLoginData(loginEntity)
                if (response.isSuccessful) {
                    val responseMap = response.body()
                    if (responseMap != null) {
                        val token = "Bearer " + (responseMap["token"] as? String ?: "")
                        val empId = (responseMap["empId"] as? String)?.toInt() ?: 0
                        val positionId = (responseMap["positionId"] as? String)?.toInt() ?: 0
                        val empName = responseMap["empName"] as? String ?: ""
                        val departmentId = (responseMap["departmentId"] as? String)?.toInt() ?: 0

                        val loginResponse = LoginResponseDto(token, empId, positionId, empName, departmentId)

                        withContext(Dispatchers.Main) {
                            //Toast.makeText(this@Login, "Login successful, token: $token", Toast.LENGTH_SHORT).show()
                            loginRepository.dropLoginTable()
                            loginRepository.insertTokenData(loginResponse)
                        }

                        return@withContext loginResponse
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@Login, "Login failed: empty response", Toast.LENGTH_SHORT).show()
                        }
                        return@withContext null
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Login, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                    return@withContext null
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Login, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                return@withContext null
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Login, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                return@withContext null
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Login, "Error: 아이디/비밀번호를 확인해 주세요", Toast.LENGTH_SHORT).show()
                }
                return@withContext null
            }
        }
    }

    private fun updateEmpData(token: String) {
        empService = RetrofitInstanceEMP.api

        // 데이터베이스에 데이터가 있는지 확인
        val empCount = empRepository.getEmpCount()
        if (empCount > 0) {
            // 데이터가 이미 존재하면 업데이트하지 않음
            return
        }

        empService.getEmpData(token).enqueue(object : Callback<EmpAndroidDTO> {
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
//                        Toast.makeText(this@Login, "업데이트 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("UpdateEmpData", "empDtoList is null")
                        Toast.makeText(this@Login, "업데이트 실패, 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e(
                        "UpdateEmpData",
                        "Response not successful: ${response.errorBody()?.string()}"
                    )
                    Toast.makeText(this@Login, "업데이트 실패, 다시 시도해 주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EmpAndroidDTO>, t: Throwable) {
                Log.e("UpdateEmpData", "Request failed: ${t.message}")
                Toast.makeText(this@Login, "업데이트 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private suspend fun updateCalendar(empId: Int, token: String) {
        try {
            val response = withContext(Dispatchers.IO) {
                calendarService.getCalendarData(token, empId)
            }
            response.forEach { calendarData ->
                calendarRepository.insertCalendarInDatabase(calendarData)
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(this@Login, "캘린더 데이터 업데이트 성공", Toast.LENGTH_SHORT).show()
            }
        } catch (e: HttpException) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@Login,
                    "캘린더 데이터 가져오기 실패: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: IOException) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@Login, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private suspend fun updateApproval(empId: Int, token: String) {
        // 전자결재 업데이트 로직
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

//                            Toast.makeText(this@Login, "데이터 삽입 완료", Toast.LENGTH_SHORT).show()

                        }
                    } else {

                        Toast.makeText(this@Login, "데이터 가져오기 실패", Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<ContanctAandroidDTO>, t: Throwable) {

                    Toast.makeText(this@Login, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT)
                        .show()

                }
            })
    }
}