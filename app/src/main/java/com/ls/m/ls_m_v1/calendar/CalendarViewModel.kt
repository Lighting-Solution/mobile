package com.ls.m.ls_m_v1.calendar

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.ls.m.ls_m_v1.calendar.dto.CalendarEvent
import com.ls.m.ls_m_v1.calendar.entity.CalendarEntity
import com.ls.m.ls_m_v1.calendar.repository.CalendarRepository
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.p_contect.service.contectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val calendarRepository = CalendarRepository(application)
    private val _events = MutableLiveData<List<CalendarEvent>>()
    val events: LiveData<List<CalendarEvent>> get() = _events

    private val eventColors = listOf(
        Color.parseColor("#87CEEB"),
        Color.parseColor("#FFC0CB"),
        Color.parseColor("#F08080"),
        Color.parseColor("#FFDAB9"),
        Color.parseColor("#9370DB"),
        Color.parseColor("#90EE90")
    )
    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            val datas = calendarRepository.getAllCalendar()
            val eventsData = mutableListOf<CalendarEvent>()

            for ((index, data) in datas.withIndex()) {
                val startDateTime = data.calendarStartAt.split(" ")
                val endDateTime = data.calendarEndAt.split(" ")
                val event = CalendarEvent(
                    title = data.calendarTitle,
                    startDate = LocalDate.parse(startDateTime[0]),
                    endDate = LocalDate.parse(endDateTime[0]),
                    startTime = startDateTime.getOrNull(1) ?: "",
                    endTime = endDateTime.getOrNull(1) ?: "",
                    color = eventColors[index % eventColors.size],
                    contants = data.calendarContent,
                    allDay = data.allDay
                )

                eventsData.add(event)
            }

            _events.postValue(eventsData)
        }
    }

    fun getEventsOnDate(date: LocalDate): List<CalendarEvent> {
        return _events.value?.filter { event ->
            event.startDate <= date && date <= event.endDate
        } ?: emptyList()
    }
}

//class CalendarViewModel(application: Application) : AndroidViewModel(application) {
//    private val dbHelper = DatabaseHelper(application)
//    private val _events = MutableLiveData<List<CalendarEvent>>()
//    val events: LiveData<List<CalendarEvent>> get() = _events
//
//    init {
//        // 필요한 초기화 로직이 있다면 여기에 추가합니다.
//    }
//
//    //    fun loadEvents(date: LocalDate) {
////        val entities = dbHelper.getCalendarData(date)
////
////        val events = entities.map { entity -> entity.toCalendarEvent() }
////        _events.value = events
////    }
//    fun loadEvents(): List<CalendarEvent> {
//        val datas = dbHelper.getAllCalendar()
//        val eventsData = mutableListOf<CalendarEvent>()  // 빈 리스트로 초기화
//
//        for (data in datas) {
//            val startDateTime = data.calendarStartAt.split(" ")
//            val endDateTime = data.calendarEndAt.split(" ")
//            val event = CalendarEvent(
//                title = data.calendarTitle,
//                startDate = LocalDate.parse(startDateTime[0]),
//                endDate = LocalDate.parse(endDateTime[0]),
//                startTime = startDateTime.getOrNull(1) ?: "",
//                endTime = endDateTime.getOrNull(1) ?: ""
//            )
//            eventsData.add(event)  // 리스트에 이벤트 추가
//        }
//
//        return eventsData
//    }
//
//
//    fun getEventsOnDate(date: LocalDate): List<CalendarEvent> {
//        return _events.value?.filter { it.startDate <= date && it.endDate >= date } ?: emptyList()
//    }
//
//
//
//    private fun CalendarEntity.toCalendarEvent(): CalendarEvent {
//        val startDateTime = this.calendarStartAt.split(" ")
//        val endDateTime = this.calendarEndAt.split(" ")
//
//        return CalendarEvent(
//            title = this.calendarTitle,
//            startDate = LocalDate.parse(startDateTime[0]),
//            endDate = LocalDate.parse(endDateTime[0]),
//            startTime = startDateTime.getOrNull(1) ?: "",
//            endTime = endDateTime.getOrNull(1) ?: ""
//        )
//    }
//}
////    fun getEventsOnDate(date: LocalDate): List<CalendarEvent> {
////        val entities = dbHelper.getCalendarData(date)
////        return entities.map { entity ->
////            CalendarEvent(
////                // 데이터 받아올땐 T 로 바꿀것
////                title = entity.calendarTitle,
////                startDate = LocalDate.parse(entity.calendarStartAt.split(" ")[0]),
////                endDate = LocalDate.parse(entity.calendarEndAt.split(" ")[0]),
////                startTime = entity.calendarStartAt.split(" ")[1],
////                endTime = entity.calendarEndAt.split(" ")[1]
////            )
////        }
////    }
//
//
///*
//        fun onDateSelected(date: LocalDate) : ArrayList<CalendarEvent> {
//        // 날짜가 선택되었을 때 처리할 로직을 여기에 추가합니다.
//        // 예를 들어, 특정 날짜에 해당하는 이벤트를 로드하는 코드를 작성할 수 있습니다.
//        var eventEntity: ArrayList<CalendarEntity>
//        eventEntity = repository.getCalendarData(date)
//        for (entity in eventEntity){
//            val value = CalendarEvent(
//                title = entity.calendarTitle,
//                startDate = LocalDate.parse(entity.calendarStartAt.split("T")[0]),
//                endDate = LocalDate.parse(entity.calendarEndAt.split("T")[0]),
//                startTime = entity.calendarStartAt.split("T")[1],
//                endTime = entity.calendarEndAt.split("T")[1]
//            )
//            _events.value.add(value)
//        }
//        return _events
//    }*/
////

