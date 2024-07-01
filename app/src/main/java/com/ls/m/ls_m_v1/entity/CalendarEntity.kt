package com.ls.m.ls_m_v1.entity

import java.time.LocalDateTime

data class CalendarEntity (
    var calendarId: Int,
    var calendarTitle: String,
    var calendarCreateAt: String,
    var calendarContent: String,
    var calendarStartAt:String,
    var calendarEndAt: String
)
