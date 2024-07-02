package com.ls.m.ls_m_v1.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ls.m.ls_m_v1.MainActivity
import com.ls.m.ls_m_v1.R

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginiButton = findViewById<Button>(R.id.loginButton)
        loginiButton.setOnClickListener {
            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
// 로그인이 될때 데이터 베이스 날릴 것