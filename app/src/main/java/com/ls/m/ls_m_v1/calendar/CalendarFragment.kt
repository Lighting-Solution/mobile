package com.ls.m.ls_m_v1.calendar

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.yearMonth
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.login.entity.LoginResponseDto
import com.ls.m.ls_m_v1.login.repository.LoginRepository
import java.io.Serializable
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
    private lateinit var addButton: FloatingActionButton
    private var selectedDate: LocalDate? = null
    private lateinit var calendarAdapter: CalendarAdapter
    private val calendarViewModel: CalendarViewModel by viewModels()
    private lateinit var loginRepository: LoginRepository
    private lateinit var loginData: LoginResponseDto

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
        addButton = view.findViewById(R.id.AddButton)

        loginRepository = LoginRepository(requireContext())

        loginData = loginRepository.getloginData()

        setupRecyclerView()
        setupCalendarView()
        showEventsForDate(LocalDate.now()) // 현재 날짜의 이벤트를 미리 보여줌

        addButton.setOnClickListener {
            val intent = Intent(requireContext(), AddCalendar::class.java).apply {
                putExtra("loginData", loginData as Serializable)
            }
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        calendarAdapter = CalendarAdapter()
        eventsRecyclerView.adapter = calendarAdapter
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

        calendarViewModel.events.observe(viewLifecycleOwner, Observer { events ->
            calendarView.notifyCalendarChanged()
            if (selectedDate != null) {
                showEventsForDate(selectedDate!!)
            }
        })

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                container.textView.text = data.date.dayOfMonth.toString()

                // 이벤트 여부와 상관없이 항상 updateEventViews 실행
                context?.let { updateEventViews(container.viewContainer, data.date, it) }

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
                        if (oldDate != null) {
                            calendarView.notifyDateChanged(oldDate)
                        }
                        calendarView.notifyDateChanged(data.date)
                        showEventsForDate(data.date) // 선택된 날짜의 이벤트를 표시
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

    private fun updateEventViews(viewContainer: LinearLayout, date: LocalDate, context: Context) {
        viewContainer.removeAllViews()
        val events = calendarViewModel.getEventsOnDate(date)
        val maxLine = 5

        events.take(maxLine).forEach { event ->
            val eventView = View(context)
            val color = event.color

            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(color)
                cornerRadius = 5f
            }

            eventView.background = drawable

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                5 // 높이는 필요에 따라 조정
            ).apply {
                setMargins(4, 4, 4, 4)
            }

            eventView.layoutParams = layoutParams
            viewContainer.addView(eventView)
        }
    }


    private fun showEventsForDate(date: LocalDate) {
        val eventsForDate = calendarViewModel.getEventsOnDate(date)
        calendarAdapter.submitList(eventsForDate)
    }
}
