package com.ls.m.ls_m_v1

import EMPFragment
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ls.m.ls_m_v1.calendar.CalendarFragment

class MainActivity : AppCompatActivity() {
    lateinit var calendarFragment : CalendarFragment
    lateinit var empFragment: EMPFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarFragment = CalendarFragment()
        empFragment = EMPFragment()

        supportFragmentManager.beginTransaction().replace(R.id.container, empFragment).commit()

        val buttonNavView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        buttonNavView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.tab1 -> {
                    Toast.makeText(applicationContext, "첫 번째 탭", Toast.LENGTH_SHORT).show()
                    supportFragmentManager.beginTransaction().replace(R.id.container, empFragment).commit()
                    true
                }
                R.id.tab2 -> {
                    Toast.makeText(applicationContext, "두 번째 탭", Toast.LENGTH_SHORT).show()
                    supportFragmentManager.beginTransaction().replace(R.id.container, calendarFragment).commit()
                    true
                }

                else -> {false}
            }
        }



    }


}