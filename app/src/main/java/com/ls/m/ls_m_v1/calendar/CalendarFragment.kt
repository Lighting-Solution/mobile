package com.ls.m.ls_m_v1.calendar

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.yearMonth
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.ls.m.ls_m_v1.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import java.util.Random

class CalendarFragment : Fragment() {
    lateinit var calendarView: CalendarView
    private lateinit var monthTitle: TextView
    private lateinit var selectedDateView: TextView
    private var selectedDate: LocalDate? = null

    private lateinit var calendarViewModel: CalendarViewModel

    private val eventColors =
        listOf(Color.RED, Color.BLUE, Color.GREEN, Color.LTGRAY, Color.DKGRAY, Color.MAGENTA)
    private val random = Random()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_calender, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendarView = view.findViewById(R.id.calendarView)
        monthTitle = view.findViewById(R.id.monthTitle)
        selectedDateView = view.findViewById(R.id.selectedDateView)
        selectedDate = LocalDate.now()

        calendarViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(CalendarViewModel::class.java)

        setupCalendarView()
    }

    private fun setupCalendarView() {
        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
        calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        val titleContainer = view?.findViewById<LinearLayout>(R.id.titlesContainer)
        titleContainer?.children?.forEachIndexed { index, view ->
            val textView = view as TextView
            textView.text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.getDefault())

            when (daysOfWeek[index]) {
                DayOfWeek.SATURDAY -> textView.setTextColor(Color.BLUE)
                DayOfWeek.SUNDAY -> textView.setTextColor(Color.RED)
                else -> textView.setTextColor(Color.BLACK)
            }
        }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                container.textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    when (data.date.dayOfWeek) {
                        DayOfWeek.SATURDAY -> container.textView.setTextColor(Color.BLUE)
                        DayOfWeek.SUNDAY -> container.textView.setTextColor(Color.RED)
                        else -> container.textView.setTextColor(Color.BLACK)
                    }

                    if (data.date == selectedDate) {
                        selectedDateView.text = "${data.date.monthValue}월 ${data.date.dayOfMonth}일"
                        container.dayLayout.setBackgroundResource(R.drawable.selected_day_background)
                    } else {
                        container.dayLayout.setBackgroundResource(0)
                    }

                    container.dayLayout.setOnClickListener {
                        val oldDate = selectedDate
                        selectedDate = data.date
                        Toast.makeText(requireContext(), "${data.date}", Toast.LENGTH_SHORT).show()
                        if (oldDate != null) {
                            calendarView.notifyDateChanged(oldDate)
                        }
                        calendarView.notifyDateChanged(data.date)
                        updateEventViews(container.viewContainer, data.date)
                    }


                } else {
                    container.textView.setTextColor(Color.GRAY)
                    container.dayLayout.setBackgroundResource(0)

                    container.dayLayout.setOnClickListener {
                        selectedDate = data.date
                        calendarView.smoothScrollToMonth(data.date.yearMonth)
                        calendarView.post {
                            selectedDate?.let { date ->
                                calendarView.notifyDateChanged(date)
                            }
                        }
                    }
                }

                val today = LocalDate.now()
                if (today == data.date) {
                    container.textView.setBackgroundResource(R.drawable.today_bg)
                    container.textView.setTextColor(Color.WHITE)
                }else{
                    container.textView.setBackgroundResource(0)
                }
                updateEventViews(container.viewContainer, data.date)
            }
        }

        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {}
        }

        calendarView.monthScrollListener = {
            updateMonthTitle(it)
        }
    }

    private fun updateMonthTitle(month: CalendarMonth) {
        val title = month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        monthTitle.text = title
    }

    private fun updateEventViews(viewContainer: LinearLayout, date: LocalDate) {
        viewContainer.removeAllViews()
        val events = calendarViewModel.getEventsOnDate(date)
        Log.d("CalendarFragment", "Events on $date: $events")
        val usedColors = mutableSetOf<Int>()
        val maxLine = 5

        events.take(maxLine).forEach { event ->
            val eventView = View(context)
            val color = getRandomColor(usedColors)
            usedColors.add(color)

            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(color)
                cornerRadius = 5f
            }

            eventView.background = drawable

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                5
            ).apply {
                setMargins(4, 4, 4, 4)
            }

            eventView.layoutParams = layoutParams
            viewContainer.addView(eventView)
        }
    }

    private fun getRandomColor(usedColors: Set<Int>): Int {
        val availableColors = eventColors.filter { it !in usedColors }
        return if (availableColors.isNotEmpty()) {
            availableColors[random.nextInt(availableColors.size)]
        } else {
            eventColors[random.nextInt(eventColors.size)]
        }
    }
}
