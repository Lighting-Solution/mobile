package com.ls.m.ls_m_v1.emp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.emp.entity.*

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val _contacts = MutableLiveData<List<Any>>()

    val contacts: LiveData<List<Any>> get() = _contacts

    private val dbHelper = DatabaseHelper(application)

    init {
        loadContacts()

    }
    private fun loadContacts() {
        val empList = dbHelper.getAllEmps()
        val contacts = mutableListOf<Any>()

        // 부서와 팀을 구분하여 그룹화
        val departments = empList.filter { it.department.departmentId % 10 == 0 }
        val teams = empList.filter { it.department.departmentId % 10 != 0 }

        for (department in departments) {
            // 부서 섹션 추가
            contacts.add(SectionHeader(department.department.departmentName))

            // 해당 부서의 팀들을 찾아 그룹화
            val departmentTeams = teams.filter {
                it.department.departmentId / 10 == department.department.departmentId / 10
            }

            for (team in departmentTeams) {
                // 팀 섹션 추가
                contacts.add(SectionHeader("  ${team.department.departmentName}"))

                // 팀원 추가
                contacts.addAll(teams.filter { it.department.departmentId == team.department.departmentId }
                    .map { emp ->
                        AllContact(
                            emp.empId.toString(),
                            emp.empName,
                            emp.position.positionName,
                            emp.department.departmentName,
                            emp.empEmail,
                            emp.empMP,
                            emp.company.companyNumber,
                            emp.empBirthday.toString(),
                            emp.company.companyName
                        )
                    })
            }
        }
        _contacts.value = contacts
    }


//    private fun loadContacts() {
//        val empList = dbHelper.getAllEmps()
//        val contacts = mutableListOf<Any>()
//
//        // 데이터를 적절히 분류하여 SectionHeader와 함께 추가
//        val sections = empList.groupBy { it.department.departmentName }
//        for ((section, items) in sections) {
//            contacts.add(SectionHeader(section))
//            contacts.addAll(items.map { emp ->
//                AllContact(emp.empId.toString(), emp.empName, emp.position.positionName, emp.department.departmentName, emp.empEmail, emp.empMP, emp.company.companyNumber, emp.empBirthday.toString(), emp.company.companyName)
//            })
//        }
//
//        _contacts.value = contacts
//    }
}
// 이런 식으로 api가능
//    private val calendarApi: CalendarApi
//
//    init {
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://your-database-api.com/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        calendarApi = retrofit.create(CalendarApi::class.java)
//
//        loadContacts()
//    }
//
//    private fun loadContacts() {
//        viewModelScope.launch {
//            try {
//                val response = calendarApi.getEvents().execute()
//                if (response.isSuccessful) {
//                    val events = response.body()?.map { event ->
//                        ListItem.Contact(event.id, event.name, event.position, event.department)
//                    } ?: emptyList()
//                    val contactsWithSections = mutableListOf<ListItem>()
//
//                    // 부서별로 그룹화하여 섹션 헤더 추가
//                    events.groupBy { it.department }.forEach { (department, contacts) ->
//                        contactsWithSections.add(ListItem.SectionHeader(department))
//                        contactsWithSections.addAll(contacts)
//                    }
//
//                    _contacts.value = contactsWithSections
//                }
//            } catch (e: Exception) {
//                // 에러 처리
//            }
//        }
//    }
