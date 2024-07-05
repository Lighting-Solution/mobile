package com.ls.m.ls_m_v1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.ls.m.ls_m_v1.login.Login

class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        val dbHelper = DatabaseHelper(this)
//        dbHelper.writableDatabase


        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@Splash, Login::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}