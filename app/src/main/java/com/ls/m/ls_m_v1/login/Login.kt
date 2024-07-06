package com.ls.m.ls_m_v1.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ls.m.ls_m_v1.MainActivity
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.calendar.service.RetrofitInstanceCalender
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.login.entity.LoginEntity
import com.ls.m.ls_m_v1.login.service.RetrofitInstanceLogin
import com.ls.m.ls_m_v1.repository.CalendarRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : AppCompatActivity() {
    private lateinit var id: EditText
    private lateinit var pw: EditText
    private lateinit var loginButton: Button

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var calendarRepository: CalendarRepository

    //    private lateinit var sessionManager: SessionManager
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        id = findViewById(R.id.userID)
        pw = findViewById(R.id.userPassword)
        loginButton = findViewById(R.id.loginButton)

        // 인스턴스 생성하고 데이터베이스 액세스

//        calendarRepository = CalendarRepository(this)


        //  session
        loginButton.setOnClickListener {
//            val userId = id.text.toString()
//            val userPw = pw.text.toString()
//            login(userId, userPw)
            // 로그인 구현할땐 삭제 (intent, update)

//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val datas = RetrofitInstanceCalender.api.requestCalendarData()
//                        dbHelper.onDelete(DatabaseHelper.CALENDAR_TABLE)
//                        calendarRepository.insertCalendarData(datas)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
            dbHelper = DatabaseHelper(this)
            dbHelper.writableDatabase

            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun login(userId: String, userPw: String) {
        val loginEntity = LoginEntity(userId, userPw)

        //코루틴을 사용하여 네트워크 요청을 비동기로 처리
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 데이터 통신이 된다면 데이터 베이스 삭제 후 다시 업로드
                val response = RetrofitInstanceLogin.api.requestLoginData(loginEntity)
                withContext(Dispatchers.Main) {
                    // 세션에 토큰 저장
                    //sessionManager.saveAuthToken(response.token)
                    Toast.makeText(
                        this@Login,
                        "Login successful, token:$'{response.token}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // 다음작업 처리

                    // 데이터 베이스 날리고
                    // 동기화 처리

                    // 화면전환
                    val intent = Intent(this@Login, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
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