package com.ls.m.ls_m_v1

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ls.m.ls_m_v1.approval.ApprovalFragment
import com.ls.m.ls_m_v1.approval.repository.ApprovalRepository
import com.ls.m.ls_m_v1.approval.service.ApprovalService
import com.ls.m.ls_m_v1.approval.service.RetrofitInstanceApproval
import com.ls.m.ls_m_v1.calendar.CalendarFragment
import com.ls.m.ls_m_v1.calendar.repository.CalendarRepository
import com.ls.m.ls_m_v1.calendar.service.CalendarService
import com.ls.m.ls_m_v1.calendar.service.RetrofitInstanceCalender
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.emp.EMPFragment
import com.ls.m.ls_m_v1.emp.entity.EmpAndroidDTO
import com.ls.m.ls_m_v1.emp.repository.EmpRepository
import com.ls.m.ls_m_v1.emp.service.EMPService
import com.ls.m.ls_m_v1.emp.service.RetrofitInstanceEMP
import com.ls.m.ls_m_v1.login.repository.LoginRepository
import com.ls.m.ls_m_v1.p_contect.InterpersonalFragment
import com.ls.m.ls_m_v1.p_contect.entity.ContanctAandroidDTO
import com.ls.m.ls_m_v1.p_contect.repository.PersonalContactRepository
import com.ls.m.ls_m_v1.p_contect.service.P_ContectService
import com.ls.m.ls_m_v1.p_contect.service.RetrofitInstancePersonal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var calendarFragment: CalendarFragment
    lateinit var empFragment: EMPFragment
    lateinit var interpersonalFragment: InterpersonalFragment
    lateinit var approvalFragment: ApprovalFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarFragment = CalendarFragment()
        empFragment = EMPFragment()
        interpersonalFragment = InterpersonalFragment()
        approvalFragment = ApprovalFragment()

        // 초기 화면전환
        supportFragmentManager.beginTransaction().replace(R.id.container, empFragment).commit()

        val buttonNavView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        buttonNavView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab1 -> {
                    Toast.makeText(applicationContext, "첫 번째 탭", Toast.LENGTH_SHORT).show()

                    supportFragmentManager.beginTransaction().replace(R.id.container, empFragment)
                        .commit()

                    true
                }

                R.id.tab2 -> {
                    Toast.makeText(applicationContext, "두 번째 탭", Toast.LENGTH_SHORT).show()

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, interpersonalFragment).commit()

                    true
                }

                R.id.tab3 -> {
                    Toast.makeText(applicationContext, "세 번째 탭", Toast.LENGTH_SHORT).show()

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, calendarFragment).commit()

                    true
                }

                R.id.tab4 -> {
                    Toast.makeText(applicationContext, "네 번째 탭", Toast.LENGTH_SHORT).show()


                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, approvalFragment).commit()

                    true
                }
                else -> false
            }
        }


    }
}
