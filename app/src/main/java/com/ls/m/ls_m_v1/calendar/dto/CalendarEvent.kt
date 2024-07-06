package com.ls.m.ls_m_v1.calendar.dto

import java.time.LocalDate

data class CalendarEvent(
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val startTime: String,
    val endTime: String,
    val color: Int
)

