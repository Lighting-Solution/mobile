package com.ls.m.ls_m_v1.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.calendar.dto.CalendarEvent

class CalendarAdapter(private val calendarEvent: List<CalendarEvent>) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calendar_event, parent, false)
        return CalendarViewHolder(view)
    }

    override fun getItemCount(): Int {
        return calendarEvent.size
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val event = calendarEvent[position]
        holder.bind(event)
    }

    class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val eventTitleText: TextView = view.findViewById(R.id.eventTitleText)
        private val eventTimeText: TextView = view.findViewById(R.id.eventTime)

        fun bind(event: CalendarEvent) {
            eventTitleText.text = event.title
            if (event.startDate == event.endDate) {
                eventTimeText.text = "${event.startTime} ~ ${event.endTime}"
            }else{
                eventTimeText.text = "${event.startDate} ${event.startTime} - ${event.endDate} ${event.endTime}"
            }
        }
    }
}
