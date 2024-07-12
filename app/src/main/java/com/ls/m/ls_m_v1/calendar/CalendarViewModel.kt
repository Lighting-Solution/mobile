package com.ls.m.ls_m_v1.calendar

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ls.m.ls_m_v1.calendar.dto.CalendarEvent
import com.ls.m.ls_m_v1.calendar.entity.CalendarDto
import com.ls.m.ls_m_v1.calendar.repository.CalendarRepository
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

    fun loadEvents() {
        val datas = calendarRepository.getAllCalendar()
        viewModelScope.launch(Dispatchers.IO) {
            val eventsData = mutableListOf<CalendarEvent>()

            for ((index, data) in datas.withIndex()) {
                val startDateTime = data.calendarStartAt.split("T")
                val endDateTime = data.calendarEndAt.split("T")
                val event = CalendarEvent(
                    title = data.calendarTitle,
                    startDate = LocalDate.parse(startDateTime[0]),
                    endDate = LocalDate.parse(endDateTime[0]),
                    startTime = startDateTime.getOrNull(1) ?: "",
                    endTime = endDateTime.getOrNull(1) ?: "",
                    color = eventColors[index % eventColors.size],
                    contants = data.calendarContent,
                    allDay = data.allDay == 1
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

    fun addEvent(calendarEvent: CalendarEvent) {
        val calDTO = CalendarDto(
            calendarTitle = calendarEvent.title,
            calendarCreateAt = calendarEvent.startDate.toString(),
            calendarContent = calendarEvent.contants,
            calendarStartAt = "${calendarEvent.startDate}T${calendarEvent.startTime}",
            calendarEndAt = "${calendarEvent.endDate}T${calendarEvent.endTime}",
            allDay = if (calendarEvent.allDay) 1 else 0,
            attendees = listOf() // 필요에 따라 업데이트
        )

        viewModelScope.launch(Dispatchers.IO) {
            calendarRepository.createCalendarInDatabase(calDTO)
            loadEvents() // 새 이벤트를 추가한 후 이벤트를 새로고침
        }
    }
}
