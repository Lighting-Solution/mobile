package com.ls.m.ls_m_v1.calendar

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

class CustomTitleFormatter : TitleFormatter {
    private val dateFormat : DateFormat = SimpleDateFormat("yyyy MMMM", Locale.getDefault())
    override fun format(day: CalendarDay?): CharSequence {
        val calendar = day?.calendar
        return dateFormat.format(calendar?.time)
    }

}