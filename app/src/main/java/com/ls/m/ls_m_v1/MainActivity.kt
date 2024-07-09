package com.ls.m.ls_m_v1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.replace
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ls.m.ls_m_v1.approval.ApprovalFragment
import com.ls.m.ls_m_v1.calendar.CalendarFragment
import com.ls.m.ls_m_v1.emp.EMPFragment
import com.ls.m.ls_m_v1.p_contect.InterpersonalFragment

class MainActivity : AppCompatActivity() {
    lateinit var calendarFragment : CalendarFragment
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

        supportFragmentManager.beginTransaction().replace(R.id.container, empFragment).commit()

        val buttonNavView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        buttonNavView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.tab1 -> {
                    Toast.makeText(applicationContext, "첫 번째 탭", Toast.LENGTH_SHORT).show()
                    //emp는 초기에 들고옴. 그래도 다시 받아오는게 나은가..?
                    // 그럼 몇개 테이블을 날려야하지..?
                    // emp, company, department,
                    supportFragmentManager.beginTransaction().replace(R.id.container, empFragment).commit()
                    true
                }
                R.id.tab2 -> {
                    Toast.makeText(applicationContext, "두 번째 탭", Toast.LENGTH_SHORT).show()

                    supportFragmentManager.beginTransaction().replace(R.id.container, interpersonalFragment).commit()
                    true
                }
                R.id.tab3 -> {
                    Toast.makeText(applicationContext, "세 번째 탭", Toast.LENGTH_SHORT).show()
                    supportFragmentManager.beginTransaction().replace(R.id.container, calendarFragment).commit()
                    true
                }
                R.id.tab4 -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, approvalFragment).commit()
                    true
                }

                else -> {false}
            }
        }



    }


}