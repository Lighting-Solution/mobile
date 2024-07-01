package com.ls.m.ls_m_v1.calendar

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
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
    private lateinit var monthTitle : TextView
//    private lateinit var repository: CalendarRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calender)

        calendarView = findViewById(R.id.calendarView)
        monthTitle = findViewById(R.id.monthTitle)
        setupCalendarView()
    }

    private fun setupCalendarView() {
        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.textView.text = data.date.dayOfMonth.toString()

                val displayedMonthYear = YearMonth.from(data.date) // 현재 달의 YearMonth 값
                val dayMonthYear = YearMonth.from(data.date) // 현재 날짜의 YearMonth 값

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

                val today = LocalDate.now()
                if (today == data.date) {
                    container.textView.setBackgroundResource(R.drawable.today_bg)
                    container.textView.setTextColor(Color.WHITE)
                }

            }


            override fun create(view: View): DayViewContainer {
                // 새로운 DayViewContainer 인스턴스를 생성하고 반환
                return DayViewContainer(view, this@CalendarMainActivity)
            }

        }

        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
//                val currentYear = YearMonth.now().year
//                val year = data.yearMonth.year
//                val title = data.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
//                container.monthTitle.text = if (year != currentYear) "${year}년 $title" else title
            }
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
            val title = month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
            monthTitle.text = title
        }


    }
}


