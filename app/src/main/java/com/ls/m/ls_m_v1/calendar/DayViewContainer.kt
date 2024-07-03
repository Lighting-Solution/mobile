package com.ls.m.ls_m_v1.calendar

import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer
import com.ls.m.ls_m_v1.R

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView: TextView = view.findViewById(R.id.calendarDayText)
    val dayLayout: FrameLayout = view.findViewById(R.id.DayfirstLayout)
    val viewContainer: LinearLayout = view.findViewById(R.id.view_container)
    lateinit var day: CalendarDay
}
