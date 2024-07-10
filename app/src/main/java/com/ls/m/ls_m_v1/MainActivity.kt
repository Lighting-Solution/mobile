package com.ls.m.ls_m_v1

import android.os.Bundle
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
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var calendarFragment: CalendarFragment
    lateinit var empFragment: EMPFragment
    lateinit var interpersonalFragment: InterpersonalFragment
    lateinit var approvalFragment: ApprovalFragment

    private lateinit var empRepository: EmpRepository
    private lateinit var personalContactRepository: PersonalContactRepository
    private lateinit var calendarRepository: CalendarRepository
    private lateinit var approvalRepository: ApprovalRepository
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var loginRepository: LoginRepository

    private lateinit var empService: EMPService
    private lateinit var p_contactService :P_ContectService
    private lateinit var calendarService: CalendarService
    private lateinit var approvalService: ApprovalService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarFragment = CalendarFragment()
        empFragment = EMPFragment()
        interpersonalFragment = InterpersonalFragment()
        approvalFragment = ApprovalFragment()

        // 데이터베이스와 레포 초기화
        empRepository = EmpRepository(this)
        personalContactRepository = PersonalContactRepository(this)
        calendarRepository = CalendarRepository(this)
        approvalRepository = ApprovalRepository(this)
        dbHelper = DatabaseHelper(this)
        loginRepository = LoginRepository(this)

        // 서비스 초기화
        empService = RetrofitInstanceEMP.api
        p_contactService = RetrofitInstancePersonal.api
        calendarService = RetrofitInstanceCalender.api
        approvalService = RetrofitInstanceApproval.instance

        val loginData = loginRepository.getloginData()

        // 초기 화면전환
        supportFragmentManager.beginTransaction().replace(R.id.container, empFragment).commit()

        val buttonNavView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        buttonNavView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab1 -> {
                    Toast.makeText(applicationContext, "첫 번째 탭", Toast.LENGTH_SHORT).show()
                    //emp는 초기에 들고옴. 그래도 다시 받아오는게 나은가..?
                    // 그럼 몇개 테이블을 날려야하지..?
                    // emp, department,position // company는 날리지않아도..
                    empRepository.forRefreash()
                    updateEmpData()

                    supportFragmentManager.beginTransaction().replace(R.id.container, empFragment)
                        .commit()
                    true
                }

                R.id.tab2 -> {
                    Toast.makeText(applicationContext, "두 번째 탭", Toast.LENGTH_SHORT).show()
                    // 개인 주소록,개인그룹, 회사, 연락처 그룹
                    // 데이터 베이스 초기화하기
                    personalContactRepository.forRefresh()
                    updatePersonal(loginData.empId.toString())

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
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, approvalFragment).commit()
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun updateEmpData() {
        empService.getEmpData().enqueue(object : Callback<EmpAndroidDTO> {
            override fun onResponse(call: Call<EmpAndroidDTO>, response: Response<EmpAndroidDTO>) {
                if (response.isSuccessful) {
                    // 성공적으로 업데이트
                    Toast.makeText(this@MainActivity, "업데이트 성공", Toast.LENGTH_SHORT).show()

                    val empDtoList = response.body()?.empDTOList
                    val positionList = response.body()?.positionDTOList
                    val departmentList = response.body()?.departmentDTOList

                    if (empDtoList != null) {
                        empRepository.insertEmp(empDtoList)
                        if (positionList != null)
                            empRepository.insertPosition(positionList)

                        if (departmentList != null)
                            empRepository.insertDepartment(departmentList)
                    } else {
                        Toast.makeText(this@MainActivity, "업데이트 실패 , 데이터가 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "업데이트 실패, 다시 시도해 주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EmpAndroidDTO>, t: Throwable) {
                Toast.makeText(this@MainActivity, "업데이트 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updatePersonal(id: String) {
        p_contactService.getP_ContectData(id).enqueue(object : Callback<ContanctAandroidDTO> {
            override fun onResponse(call: Call<ContanctAandroidDTO>, response: Response<ContanctAandroidDTO>) {
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
                        Toast.makeText(this@MainActivity, "데이터 삽입 완료", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "데이터 가져오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ContanctAandroidDTO>, t: Throwable) {
                Toast.makeText(this@MainActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}