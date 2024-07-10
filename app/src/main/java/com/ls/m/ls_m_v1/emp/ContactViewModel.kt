package com.ls.m.ls_m_v1.emp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ls.m.ls_m_v1.emp.entity.AllContact
import com.ls.m.ls_m_v1.emp.entity.SectionHeader
import com.ls.m.ls_m_v1.emp.repository.EmpRepository

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val _contacts = MutableLiveData<List<Any>>()
    val contacts: LiveData<List<Any>> get() = _contacts

    private val empRepository = EmpRepository(application)

    init {
        loadContacts()
    }

    private fun loadContacts() {
        val empList = empRepository.getAllEmps()
        val contacts = mutableListOf<Any>()

        val departments = empList.filter { it.department.departmentId % 10 == 0 }
        val teams = empList.filter { it.department.departmentId % 10 != 0 }

        for (department in departments) {
            contacts.add(SectionHeader(department.department.departmentName))

            // 부서에 속한 사람들 추가
            contacts.addAll(empList.filter { it.department.departmentId == department.department.departmentId }
                .map { emp ->
                    AllContact(
                        id = emp.empId.toString(),
                        name = emp.empName,
                        position = emp.position.positionName,
                        department = emp.department.departmentName,
                        email = emp.empEmail,
                        mobilePhone = emp.empMP,
                        officePhone = emp.company.companyNumber,
                        birthday = emp.empBirthday.toString(),
                        company = emp.company,
                        buttonState = false
                    ).also {
                    }
                })

            // 부서에 속한 팀들 필터링
            val departmentTeams = teams.filter {
                it.department.departmentId / 10 == department.department.departmentId / 10
            }

            for (team in departmentTeams) {
                contacts.add(SectionHeader("   - ${team.department.departmentName}"))

                contacts.addAll(teams.filter { it.department.departmentId == team.department.departmentId }
                    .map { emp ->
                        AllContact(
                            id = emp.empId.toString(),
                            name = emp.empName,
                            position = emp.position.positionName,
                            department = emp.department.departmentName,
                            email = emp.empEmail,
                            mobilePhone = emp.empMP,
                            officePhone = emp.company.companyNumber,
                            birthday = emp.empBirthday.toString(),
                            company = emp.company,
                            buttonState = false
                        ).also {
                        }
                    })
            }
        }
        _contacts.value = contacts
    }
}
