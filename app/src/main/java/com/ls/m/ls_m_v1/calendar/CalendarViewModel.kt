package com.ls.m.ls_m_v1.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ls.m.ls_m_v1.calendar.dto.CalendarEvent
import com.ls.m.ls_m_v1.calendar.entity.CalendarEntity
import com.ls.m.ls_m_v1.repository.CalendarRepository
import java.time.LocalDate

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CalendarRepository(application)
    private val _events = MutableLiveData<List<CalendarEntity>>()
    val events: LiveData<List<CalendarEntity>> get() = _events

    fun getEventsOnDate(date: LocalDate): List<CalendarEvent> {
        val entities = repository.getCalendarData(date)
        return entities.map { entity ->
            CalendarEvent(
                title = entity.calendarTitle,
                startDate = LocalDate.parse(entity.calendarStartAt.split("T")[0]),
                endDate = LocalDate.parse(entity.calendarEndAt.split("T")[0]),
                startTime = entity.calendarStartAt.split("T")[1],
                endTime = entity.calendarEndAt.split("T")[1]
            )
        }
    }
}




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

