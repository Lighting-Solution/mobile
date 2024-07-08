package com.ls.m.ls_m_v1.calendar.entity

data class CalendarEntity (
    var calendarId: Int,
    var calendarTitle: String,
    var calendarCreateAt: String,
    var calendarContent: String,
    var calendarStartAt:String,
    var calendarEndAt: String,
    val allDay: Boolean = false
)

data class CalendarDto (
    var calendarTitle: String,
    var calendarCreateAt: String,
    var calendarContent: String,
    var calendarStartAt:String,
    var calendarEndAt: String,
    var allDay: Boolean = false
//    val user: List<SelectedUser>
)// 참석자 포함해야함