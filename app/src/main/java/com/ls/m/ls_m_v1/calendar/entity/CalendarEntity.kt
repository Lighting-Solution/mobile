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

// 삭제 아이디 만
data class CalendarDto (
    //  업데이트 id추가
    var calendarTitle: String,
    var calendarCreateAt: String,
    var calendarContent: String,
    var calendarStartAt:String,
    var calendarEndAt: String,
//    var allDay: Boolean = false,//
    val attendees: List<participantDTO>
)// 참석자 포함해야함