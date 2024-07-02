package com.ls.m.ls_m_v1.calendar

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer
import com.ls.m.ls_m_v1.R

@RequiresApi(Build.VERSION_CODES.O)
class DayViewContainer(view: View, private val context: Context) : ViewContainer(view) {
    val textView: TextView = view.findViewById(R.id.calendarDayText)
    lateinit var day: CalendarDay

    init {
        view.setOnClickListener {
            if (::day.isInitialized) {
                Toast.makeText(context, "${day.date}", Toast.LENGTH_SHORT).show()
                Log.d("DayViewContainer", "Clicked on date: ${day.date}")
            }
        }
    }
}
