package com.ls.m.ls_m_v1.p_contect

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.emp.entity.AllContact
import com.ls.m.ls_m_v1.emp.entity.SectionHeader
import com.ls.m.ls_m_v1.p_contect.repository.PersonalContactRepository

class PersonalViewModel(application: Application): AndroidViewModel(application) {
    private var personalContactRepository = PersonalContactRepository(application)
    private val _contacts = MutableLiveData<List<Any>>()
    val contacts : LiveData<List<Any>> get() = _contacts

    init {
        loadContacts()
    }

    private fun loadContacts() {
        val personalList = personalContactRepository.getAllPersonalContacts()
        val personalGroups = personalContactRepository.getAllPersonalGroups()
        val contacts = mutableListOf<Any>()

        // 데이터를 적절히 분류하여 SectionHeader와 함께 추가
        val sections = personalGroups.groupBy { it.personalGroupName }
        for ((section, groupItems) in sections) {
            contacts.add(SectionHeader(section))
            for (group in groupItems) {
                val items = personalList.filter { it.empId == group.empId }
                contacts.addAll(items.map { personal ->
                    AllContact(
                        id = personal.personalContactId.toString(),
                        name = personal.personalContactName,
                        position = personal.positionName,
                        department = personal.departmentName,
                        email = personal.personalContactEmail,
                        mobilePhone = personal.personalContactMP,
                        officePhone = personal.company.companyNumber,
                        birthday = personal.personalContactBirthday.toString(),
                        company = personal.company,
                        buttonState = true,
                        nickname = personal.personalContactNickName,
                        memo = personal.personalContactMemo
                    )
                })
            }
        }
        _contacts.value = contacts
    }
}
