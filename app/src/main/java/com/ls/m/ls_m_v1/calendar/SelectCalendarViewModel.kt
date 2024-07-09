package com.ls.m.ls_m_v1.emp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ls.m.ls_m_v1.calendar.entity.CalendarEmp
import com.ls.m.ls_m_v1.emp.entity.SectionHeader
import com.ls.m.ls_m_v1.emp.repository.EmpRepository
import com.ls.m.ls_m_v1.login.repository.LoginRepository
import kotlinx.coroutines.launch

class SelectCalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val _contacts = MutableLiveData<List<Any>>()
    val contacts: LiveData<List<Any>> get() = _contacts

    private val empRepository = EmpRepository(application)
    private val loginRepository = LoginRepository(application)

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            val empList = empRepository.getAllEmps()
            val loginId = loginRepository.getloginData()
            val contacts = mutableListOf<Any>()

            val departments = empList.filter { it.department.departmentId % 10 == 0 }
            val teams = empList.filter { it.department.departmentId % 10 != 0 }

            for (department in departments) {
                contacts.add(SectionHeader(department.department.departmentName))
                Log.d("ContactViewModel", "Added Department Header: ${department.department.departmentName}")

                // 부서에 속한 사람들 추가
                contacts.addAll(empList.filter { it.department.departmentId == department.department.departmentId }
                    .map { emp ->
                        CalendarEmp(
                            id = emp.empId.toString(),
                            name = emp.empName,
                            position = emp.position.positionName,
                            department = emp.department.departmentName,
                            mobilePhone = emp.empMP,
                            company = emp.company,
                            isSelected = emp.empId == loginId.empId
                        ).also {
                            Log.d("ContactViewModel", "Added Department Contact: $it")
                        }
                    })

                // 부서에 속한 팀들 필터링
                val departmentTeams = teams.filter {
                    it.department.departmentId / 10 == department.department.departmentId / 10
                }

                for (team in departmentTeams) {
                    contacts.add(SectionHeader("  ${team.department.departmentName}"))

                    contacts.addAll(teams.filter { it.department.departmentId == team.department.departmentId }
                        .map { emp ->
                            CalendarEmp(
                                id = emp.empId.toString(),
                                name = emp.empName,
                                position = emp.position.positionName,
                                department = emp.department.departmentName,
                                mobilePhone = emp.empMP,
                                company = emp.company,
                                isSelected = emp.empId == 1
                            )
                        })
                }
            }
            _contacts.postValue(contacts)
        }
    }
}
