package com.ls.m.ls_m_v1.calendar

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ls.m.ls_m_v1.calendar.dto.CalendarEvent
import com.ls.m.ls_m_v1.calendar.entity.CalendarEntity
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.repository.CalendarRepository
import java.time.LocalDate

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val dbHelper = DatabaseHelper(application)
    private val _events = MutableLiveData<List<CalendarEvent>>()
    val events: LiveData<List<CalendarEvent>> get() = _events

    init {
        // 필요한 초기화 로직이 있다면 여기에 추가합니다.
    }

    fun loadEvents(date: LocalDate) {
        val entities = dbHelper.getCalendarData(date)

        val events = entities.map { entity -> entity.toCalendarEvent() }
        _events.value = events
    }

    fun getEventsOnDate(date: LocalDate): List<CalendarEvent> {
        return _events.value?.filter { event ->
            !date.isBefore(event.startDate) && !date.isAfter(event.endDate)
        } ?: emptyList()
    }

    private fun CalendarEntity.toCalendarEvent(): CalendarEvent {
        val startDateTime = this.calendarStartAt.split(" ")
        val endDateTime = this.calendarEndAt.split(" ")

        return CalendarEvent(
            title = this.calendarTitle,
            startDate = LocalDate.parse(startDateTime[0]),
            endDate = LocalDate.parse(endDateTime[0]),
            startTime = startDateTime.getOrNull(1) ?: "",
            endTime = endDateTime.getOrNull(1) ?: ""
        )
    }
}
//    fun getEventsOnDate(date: LocalDate): List<CalendarEvent> {
//        val entities = dbHelper.getCalendarData(date)
//        return entities.map { entity ->
//            CalendarEvent(
//                // 데이터 받아올땐 T 로 바꿀것
//                title = entity.calendarTitle,
//                startDate = LocalDate.parse(entity.calendarStartAt.split(" ")[0]),
//                endDate = LocalDate.parse(entity.calendarEndAt.split(" ")[0]),
//                startTime = entity.calendarStartAt.split(" ")[1],
//                endTime = entity.calendarEndAt.split(" ")[1]
//            )
//        }
//    }


/*
        fun onDateSelected(date: LocalDate) : ArrayList<CalendarEvent> {
        // 날짜가 선택되었을 때 처리할 로직을 여기에 추가합니다.
        // 예를 들어, 특정 날짜에 해당하는 이벤트를 로드하는 코드를 작성할 수 있습니다.
        var eventEntity: ArrayList<CalendarEntity>
        eventEntity = repository.getCalendarData(date)
        for (entity in eventEntity){
            val value = CalendarEvent(
                title = entity.calendarTitle,
                startDate = LocalDate.parse(entity.calendarStartAt.split("T")[0]),
                endDate = LocalDate.parse(entity.calendarEndAt.split("T")[0]),
                startTime = entity.calendarStartAt.split("T")[1],
                endTime = entity.calendarEndAt.split("T")[1]
            )
            _events.value.add(value)
        }
        return _events
    }*/
//

