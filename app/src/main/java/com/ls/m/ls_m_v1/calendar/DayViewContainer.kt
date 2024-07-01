package com.ls.m.ls_m_v1.calendar

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.yearMonth
import com.kizitonwose.calendar.view.ViewContainer
import com.ls.m.ls_m_v1.R

class DayViewContainer(view: View, private val context: Context) : ViewContainer(view) {
    val textView: TextView = view.findViewById(R.id.calendarDayText)
    val dayLayout : ConstraintLayout = view.findViewById(R.id.DayLayout)
    private lateinit var day: CalendarDay

    init {
        view.setOnClickListener {
            if (::day.isInitialized) {
                Toast.makeText(context, "${day.date}", Toast.LENGTH_SHORT).show()
                Log.d("DayViewContainer", "Clicked on date: ${day.date}")


                val activity = context as CalendarMainActivity

                if (day.date.dayOfMonth != day.date.monthValue){
                    activity.calendarView.scrollToMonth(day.date.yearMonth)
                }

                activity.updateSelectDate(day.date)
            }
        }
    }
}
