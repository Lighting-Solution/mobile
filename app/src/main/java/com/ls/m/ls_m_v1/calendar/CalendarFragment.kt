package com.ls.m.ls_m_v1.calendar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

class CalendarFragment : Fragment() {
    // UI 요소와 변수 선언
    lateinit var calendarView: CalendarView
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var monthTitle: TextView
    private lateinit var selectedDateView: TextView
    private var selectedDate: LocalDate? = null
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var calendarViewModel: CalendarViewModel

    // 컬러 변수 추가
    private val eventColors =
        listOf(Color.RED, Color.BLUE, Color.GREEN, Color.LTGRAY, Color.DKGRAY, Color.MAGENTA)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_calender, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendarView = view.findViewById(R.id.calendarView)
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView)
        monthTitle = view.findViewById(R.id.monthTitle)
        selectedDateView = view.findViewById(R.id.selectedDateView)
        selectedDate = LocalDate.now()

        calendarViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(CalendarViewModel::class.java)

        setupRecyclerView()
        setupCalendarView()

        // 초기 데이터 로드
        calendarViewModel.loadEvents(selectedDate!!)
    }

    private fun setupRecyclerView() {
        eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        calendarAdapter = CalendarAdapter()
        eventsRecyclerView.adapter = calendarAdapter

        calendarViewModel.events.observe(viewLifecycleOwner) { events ->
            calendarAdapter.submitList(events)
        }
    }

    private fun setupCalendarView() {
        val daysOfWeek = daysOfWeek()  // 요일 배열 생성
        val currentMonth = YearMonth.now()  // 현재 연월 가져오기
        val startMonth = currentMonth.minusMonths(100)  // 시작 월 설정
        val endMonth = currentMonth.plusMonths(100)  // 종료 월 설정
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
                        updateEventsForDate(data.date)
                    } else {
                        container.dayLayout.setBackgroundResource(0)
                    }

                    container.dayLayout.setOnClickListener {
                        val oldDate = selectedDate
                        selectedDate = data.date
                        if (oldDate != null) {
                            calendarView.notifyDateChanged(oldDate)
                        }
                        calendarView.notifyDateChanged(data.date)

                    }

                    updateEventViews(container.viewContainer, data.date, requireContext())
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
                } else {
                    container.textView.setBackgroundResource(0)
                }
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

    private fun updateEventsForDate(date: LocalDate) {
        calendarViewModel.loadEvents(date)
    }

    private fun updateEventViews(viewContainer: LinearLayout, date: LocalDate, context: Context) {
        viewContainer.removeAllViews()
        val events = calendarViewModel.getEventsOnDate(date)
        val maxLine = 5

        events.take(maxLine).forEach { event ->
            val eventView = View(context)
            val color = getSequentialColor(date)

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

    private fun getSequentialColor(date: LocalDate): Int {
        val dayOfYear = date.dayOfYear
        return eventColors[dayOfYear % eventColors.size]
    }
}
