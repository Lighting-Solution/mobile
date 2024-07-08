package com.ls.m.ls_m_v1.emp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ls.m.ls_m_v1.calendar.entity.SelectedUser
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.emp.entity.AllContact
import com.ls.m.ls_m_v1.emp.entity.SectionHeader
import com.ls.m.ls_m_v1.emp.repository.EmpRepository

class AddCalendarViewModel(application: Application) : AndroidViewModel(application) {
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

            val departmentTeams = teams.filter {
                it.department.departmentId / 10 == department.department.departmentId / 10
            }

            for (team in departmentTeams) {
                contacts.add(SectionHeader("  ${team.department.departmentName}"))

                contacts.addAll(teams.filter { it.department.departmentId == team.department.departmentId }
                    .map { emp ->
                        SelectedUser(
                            id = emp.empId.toString(),
                            name = emp.empName,
                            position = emp.position.positionName,
                            department = emp.department.departmentName,
                            mobilePhone = emp.empMP
                        )
                    })
            }
        }
        _contacts.value = contacts
    }
}
