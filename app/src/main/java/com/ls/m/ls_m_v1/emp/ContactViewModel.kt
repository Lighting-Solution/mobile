package com.ls.m.ls_m_v1.emp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ls.m.ls_m_v1.emp.entity.AllContact
import com.ls.m.ls_m_v1.emp.entity.SectionHeader

class ContactViewModel : ViewModel() {
    private val _contacts = MutableLiveData<List<Any>>()
    val contacts : LiveData<List<Any>> get() = _contacts

    init {
        loadContacts()
    }

    private fun loadContacts() {
        val contacts = listOf(
            SectionHeader("Sales"),
            AllContact("1", "Alice", "Manager", "Sales"),
            SectionHeader("Development"),
            AllContact("2", "Bob", "Engineer", "Development"),
            SectionHeader("Design"),
            AllContact("3", "Carol", "Designer", "Design"),
            SectionHeader("Human Resources"),
            AllContact("4", "David", "HR", "Human Resources")
            // 더 많은 데이터 추가
        )
        _contacts.value = contacts
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
}