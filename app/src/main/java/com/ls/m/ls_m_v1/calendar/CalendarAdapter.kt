package com.ls.m.ls_m_v1.calendar

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.calendar.dto.CalendarEvent
import com.ls.m.ls_m_v1.calendar.entity.CalendarEmp
import java.text.SimpleDateFormat
import java.util.Locale

class CalendarAdapter() : ListAdapter<CalendarEvent, CalendarAdapter.CalendarViewHolder>(CalendarEventDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.calendar_event, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }

    class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val eventTitleText: TextView = view.findViewById(R.id.eventTitleText)
        private val eventTimeText: TextView = view.findViewById(R.id.eventTime)
        private val eventColor: View = itemView.findViewById(R.id.eventColor)


        @SuppressLint("SetTextI18n")
        fun bind(event: CalendarEvent) {
            val startTimeForm = event.startTime.substring(0,5)
            val endTimeForm = event.endTime.substring(0,5)
            if (event.allDay) {
                eventTimeText.text ="하루 종일"
            } else {
                eventTitleText.text = event.title
                if (event.startDate == event.endDate) {
                    eventTimeText.text = "${startTimeForm} ~ ${endTimeForm}"
                    eventColor.setBackgroundColor(event.color)
                } else {
                    eventTimeText.text =
                        "${event.startDate} ${startTimeForm} - ${event.endDate} ${endTimeForm}"
                    eventColor.setBackgroundColor(event.color)
                }
            }
        }
    }

    class CalendarEventDiffCallback : DiffUtil.ItemCallback<CalendarEvent>() {
        override fun areItemsTheSame(oldItem: CalendarEvent, newItem: CalendarEvent): Boolean {
            return oldItem.title == newItem.title && oldItem.startDate == newItem.startDate && oldItem.endDate == newItem.endDate
        }

        override fun areContentsTheSame(oldItem: CalendarEvent, newItem: CalendarEvent): Boolean {
            return oldItem == newItem
        }
    }
}
