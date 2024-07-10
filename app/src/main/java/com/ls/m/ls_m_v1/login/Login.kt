package com.ls.m.ls_m_v1.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.telecom.Call
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ls.m.ls_m_v1.MainActivity
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.login.entity.LoginEntity
import com.ls.m.ls_m_v1.login.entity.LoginResponseDto
import com.ls.m.ls_m_v1.login.repository.LoginRepository
import com.ls.m.ls_m_v1.login.service.RetrofitInstanceLogin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
    private lateinit var id: EditText
    private lateinit var pw: EditText
    private lateinit var loginButton: Button
    private lateinit var loginRepository: LoginRepository

    private lateinit var dbHelper: DatabaseHelper

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        id = findViewById(R.id.userID)
        pw = findViewById(R.id.userPassword)
        loginButton = findViewById(R.id.loginButton)

        // 인스턴스 생성하고 데이터베이스 액세스

        loginButton.setOnClickListener {
            val userId = id.text.toString()
            val userPw = pw.text.toString()
            // api통신으로 받아와 데이터 베이스 저장까지 완료
            login(userId, userPw)
            // 화면 전환
//            val intent = Intent(this@Login, MainActivity::class.java)
//            startActivity(intent)
//            finish()

        }

    }

    private fun login(userId: String, userPw: String) {
        loginRepository = LoginRepository(this)
        val loginEntity = LoginEntity(userId, userPw)

        //코루틴을 사용하여 네트워크 요청을 비동기로 처리
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 데이터 통신이 된다면 데이터 베이스 삭제 후 다시 업로드
                RetrofitInstanceLogin.api.requestLoginData(loginEntity)
                    .enqueue(object : Callback<LoginResponseDto> {
                        override fun onResponse(
                            call: retrofit2.Call<LoginResponseDto>,
                            response: Response<LoginResponseDto>
                        ) {
                            if (response.isSuccessful) {
                                val loginResponse = response.body()
                                if (loginResponse != null) {
                                    Toast.makeText(
                                        this@Login,
                                        "Login successful, token: ${loginResponse.token}",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    CoroutineScope(Dispatchers.IO).launch {
                                        loginRepository.dropLoginTable()
                                        loginRepository.insertTokenData(
                                            loginResponse.token,
                                            loginResponse.empId,
                                            loginResponse.positionId
                                        )
                                        // 개인 아이디가 필요한 데이터 가져오기
                                        // 캘린더, 개인 주소록, 전자결재..

                                        // 화면 전환
                                        val intent = Intent(this@Login, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()

                                    }
                                } else {
                                    Toast.makeText(
                                        this@Login,
                                        "Login failed: empty response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    this@Login,
                                    "Login failed: ${response.message()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(
                            call: retrofit2.Call<LoginResponseDto>,
                            t: Throwable
                        ) {
                            Toast.makeText(this@Login, "Error: ${t.message}", Toast.LENGTH_SHORT)
                                .show()
                        }

                    })
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // 취소 된다면 그냥 화면 전환
                    // UI 스레드에서 에러 메세지 표시
                    Toast.makeText(this@Login, "Error: 아이디/비밀번호를 확인해 주세요", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}