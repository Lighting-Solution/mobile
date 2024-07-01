package com.ls.m.ls_m_v1.dto

import java.time.LocalDate

data class CalendarEvent (
    val id : Int,
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate
)

