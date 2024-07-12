package com.ls.m.ls_m_v1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ls.m.ls_m_v1.approval.ApprovalFragment
import com.ls.m.ls_m_v1.calendar.CalendarFragment
import com.ls.m.ls_m_v1.emp.EMPFragment
import com.ls.m.ls_m_v1.p_contect.InterpersonalFragment

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

        val buttonNavView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        // 초기 화면전환
        buttonNavView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab1 -> {
                    Toast.makeText(applicationContext, "첫 번째 탭", Toast.LENGTH_SHORT).show()
                    supportFragmentManager.beginTransaction().replace(R.id.container, empFragment).commit()
                    empFragment.view?.post { empFragment.refreshData() }
                    true
                }
                R.id.tab2 -> {
                    Toast.makeText(applicationContext, "두 번째 탭", Toast.LENGTH_SHORT).show()
                    supportFragmentManager.beginTransaction().replace(R.id.container, interpersonalFragment).commit()
                    interpersonalFragment.view?.post { interpersonalFragment.refreshData() }
                    true
                }
                R.id.tab3 -> {
                    Toast.makeText(applicationContext, "세 번째 탭", Toast.LENGTH_SHORT).show()
                    supportFragmentManager.beginTransaction().replace(R.id.container, calendarFragment).commit()
                    calendarFragment.view?.post { calendarFragment.refreshData() }
                    true
                }
                R.id.tab4 -> {
                    Toast.makeText(applicationContext, "네 번째 탭", Toast.LENGTH_SHORT).show()
                    supportFragmentManager.beginTransaction().replace(R.id.container, approvalFragment).commit()
                    approvalFragment.view?.post { approvalFragment.refreshData() }
                    true
                }
                else -> false
            }
        }

        // 전달된 선택된 탭 ID 가져오기
        val selectedTabId = intent.getIntExtra("SELECTED_TAB", R.id.tab1) // 기본값은 첫 번째 탭

        // 선택된 탭 강조 표시 및 해당 프래그먼트 로드
        buttonNavView.selectedItemId = selectedTabId

        // 선택된 탭에 대한 콜백을 수동으로 호출
        when (selectedTabId) {
            R.id.tab1 -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, empFragment).commit()
                empFragment.view?.post { empFragment.refreshData() }
            }
            R.id.tab2 -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, interpersonalFragment).commit()
                interpersonalFragment.view?.post { interpersonalFragment.refreshData() }
            }
            R.id.tab3 -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, calendarFragment).commit()
                calendarFragment.view?.post { calendarFragment.refreshData() }
            }
            R.id.tab4 -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, approvalFragment).commit()
                approvalFragment.view?.post { approvalFragment.refreshData() }
            }
        }
    }
}
