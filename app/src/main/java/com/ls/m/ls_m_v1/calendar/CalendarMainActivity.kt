package com.ls.m.ls_m_v1.calendar

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.yearMonth
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.ls.m.ls_m_v1.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


class CalendarMainActivity : AppCompatActivity() {
    lateinit var calendarView: CalendarView
    private lateinit var monthTitle: TextView
    private var selectDate: LocalDate? = null

//    private lateinit var repository: CalendarRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calender)
        val view = View(this)
        calendarView = findViewById(R.id.calendarView)
        monthTitle = findViewById(R.id.monthTitle)

        // 오늘 날짜 설정
        selectDate = LocalDate.now()

        setupCalendarView()

    }
    // 선택할 때 움직이는거 만들기
    fun updateSelectDate(date: LocalDate) {
        selectDate = date
        calendarView.notifyCalendarChanged()
    }

    // 기본 틀 작성
    private fun setupCalendarView() {
        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun bind(container: DayViewContainer, data: CalendarDay) {
            }

            override fun create(view: View) = DayViewContainer(view, this@CalendarMainActivity)
        }
        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {}
        }
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(240)
        val lastMonth = currentMonth.plusMonths(240)
        val firstDayOfWeek = firstDayOfWeekFromLocale()

        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)

        val titleContainer = findViewById<LinearLayout>(R.id.titlesContainer)
        val daysOfWeek = daysOfWeek()
        titleContainer.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                val dayOfWeek = daysOfWeek[index]
                textView.text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())

                val textColor = when (dayOfWeek) {
                    DayOfWeek.SATURDAY -> Color.BLUE
                    DayOfWeek.SUNDAY -> Color.RED
                    else -> Color.BLACK
                }
                textView.setTextColor(textColor)
            }
        calendarView.monthScrollListener = { month ->
            updateMonthTitle(month)
            setupDate(month)
        }
    }

    private fun updateMonthTitle(month: CalendarMonth) {
        val currentYear = YearMonth.now().year
        val year = month.yearMonth.year
        val title = month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        monthTitle.text = if (year != currentYear) "${year}년 $title" else title
    }

    // 날짜 삽입
    private fun setupDate(month: CalendarMonth) {
        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.textView.text = data.date.dayOfMonth.toString()
                val displayedMonthYear = data.date.monthValue // 현재 달의 YearMonth 값
                val dayMonthYear = month.yearMonth.monthValue // 현재 날짜의 YearMonth 값


                val isNotInCurrentMonth = dayMonthYear != displayedMonthYear
                val dayOfWeek = data.date.dayOfWeek

                val textColor = if (isNotInCurrentMonth) {
                    Color.argb(
                        100,
                        Color.red(Color.BLACK),
                        Color.green(Color.BLACK),
                        Color.blue(Color.BLACK)
                    )
                } else {
                    when (dayOfWeek) {
                        DayOfWeek.SATURDAY -> Color.BLUE
                        DayOfWeek.SUNDAY -> Color.RED
                        else -> Color.BLACK
                    }
                }

                container.textView.setTextColor(textColor)

                //현재 날짜 표시
                val today = LocalDate.now()
                if (today == data.date) {
                    container.textView.setBackgroundResource(R.drawable.today_bg)
                    container.textView.setTextColor(Color.WHITE)
                }
                // 셀렉트 문
                if (selectDate == data.date) {
                    container.dayLayout.setBackgroundResource(R.drawable.select_day)
                } else {
                    container.dayLayout.setBackgroundResource(0)
                }

            }

            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view, this@CalendarMainActivity)
            }

        }

        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
            }
        }
    }


}



