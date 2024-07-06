package com.ls.m.ls_m_v1.emp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.emp.entity.AllContact
import com.ls.m.ls_m_v1.emp.entity.SectionHeader

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
                        AllContact(
                            emp.empId.toString(),
                            emp.empName,
                            emp.position.positionName,
                            emp.department.departmentName,
                            emp.empEmail,
                            emp.empMP,
                            emp.company.companyNumber,
                            emp.empBirthday.toString(),
                            emp.company,
                            false
                        )
                    })
            }
        }
        _contacts.value = contacts
    }
}
