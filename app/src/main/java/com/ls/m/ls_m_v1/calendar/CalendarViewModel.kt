package com.ls.m.ls_m_v1.calendar

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.ls.m.ls_m_v1.databaseHelper.CalendarRepository
import com.ls.m.ls_m_v1.dto.CalendarEvent
import com.ls.m.ls_m_v1.entity.CalendarEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val calendarRepository = CalendarRepository(application)
    private val _events = ArrayList<CalendarEvent>()

    fun onDateSelected(date: LocalDate) : ArrayList<CalendarEvent> {
        // 날짜가 선택되었을 때 처리할 로직을 여기에 추가합니다.
        // 예를 들어, 특정 날짜에 해당하는 이벤트를 로드하는 코드를 작성할 수 있습니다.
        var eventEntity = ArrayList<CalendarEntity>()
        eventEntity = calendarRepository.getCalendarData(date)
        for (entity in eventEntity){
            val value = CalendarEvent(
                title = entity.calendarTitle,
                startDate = LocalDate.parse(entity.calendarStartAt.split("T")[0]),
                endDate = LocalDate.parse(entity.calendarEndAt.split("T")[0]),
                startTime = entity.calendarStartAt.split("T")[1],
                endTime = entity.calendarEndAt.split("T")[1]
            )
            _events.add(value)
        }
        return _events
    }
}
