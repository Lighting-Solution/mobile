package com.ls.m.ls_m_v1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ls.m.ls_m_v1.calendar.CalendarMainActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calendarButton = findViewById<Button>(R.id.calendarButton)
        calendarButton.setOnClickListener {
            val intent = Intent(this, CalendarMainActivity::class.java)
            startActivity(intent)


        }


    }


}