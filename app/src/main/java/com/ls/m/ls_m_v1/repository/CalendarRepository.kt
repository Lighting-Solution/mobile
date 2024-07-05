package com.ls.m.ls_m_v1.repository

import android.content.Context
import com.ls.m.ls_m_v1.calendar.entity.CalendarEntity
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import java.time.LocalDate

class CalendarRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun getEvents(date: LocalDate): List<CalendarEntity> {
        return dbHelper.getCalendarData(date)
    }
}
