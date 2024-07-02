package com.ls.m.ls_m_v1.calendar

import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
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
import com.ls.m.ls_m_v1.databaseHelper.CalendarRepository
import com.ls.m.ls_m_v1.dto.CalendarEvent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


/*class CalendarMainActivity : AppCompatActivity() {
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


}*/
class CalendarFragment : Fragment() {
    // UI 요소와 변수 선언
    lateinit var calendarView: CalendarView
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var monthTitle: TextView
    private lateinit var selectedDateView : TextView
    private var selectedDate: LocalDate? = null
    private val eventsAdapter = EventsAdapter()
    private lateinit var calendarViewModel: CalendarViewModel


    // Fragment의 뷰를 생성하는 메소드
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_calender, container, false)
    }

    // 뷰가 생성된 후 호출되는 메소드로, 여기서 초기 설정을 수행
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendarView = view.findViewById(R.id.calendarView)
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView)
        monthTitle = view.findViewById(R.id.monthTitle)
        selectedDateView = view.findViewById(R.id.selectedDateView)
        selectedDate = LocalDate.now()
        calendarViewModel = CalendarViewModel(Application())

        val event : ArrayList<CalendarEvent> = calendarViewModel.onDateSelected(LocalDate.now())
        Log.d("ddddd", event.get(1).startTime)


        // RecyclerView와 CalendarView 설정
        setupRecyclerView()
        setupCalendarView()
    }

    // RecyclerView 설정 메소드
    private fun setupRecyclerView() {
        eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventsRecyclerView.adapter = eventsAdapter
    }

    // CalendarView 설정 메소드
    private fun setupCalendarView() {
        val daysOfWeek = daysOfWeek()  // 요일 배열 생성
        val currentMonth = YearMonth.now()  // 현재 연월 가져오기
        val startMonth = currentMonth.minusMonths(100)  // 시작 월 설정
        val endMonth = currentMonth.plusMonths(100)  // 종료 월 설정
        calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        // 타이틀 컨테이너 설정
        val titleContainer = view?.findViewById<LinearLayout>(R.id.titlesContainer)
        titleContainer?.children?.forEachIndexed { index, view ->
            val textView = view as TextView
            textView.text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.getDefault())

            // 요일 제목 색 변경
            when (daysOfWeek[index]) {
                DayOfWeek.SATURDAY -> textView.setTextColor(Color.BLUE)
                DayOfWeek.SUNDAY -> textView.setTextColor(Color.RED)
                else -> textView.setTextColor(Color.BLACK)
            }

        }

        // CalendarView의 dayBinder 설정
        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                container.textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    // 토요일 일요일 색상 변경
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
//                        updateEventsForDate(data.date)
                    }
                } else {
                    container.textView.setTextColor(Color.GRAY)
                    container.dayLayout.setBackgroundResource(0)

                    // 클릭 시 해당 월로 이동하고 선택된 날짜 유지
                    container.dayLayout.setOnClickListener {
                        selectedDate = data.date
                        calendarView.smoothScrollToMonth(data.date.yearMonth)
                        // 월 이동이 완료된 후 선택된 날짜를 갱신
                        calendarView.post{
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
                }
            }
        }

        // CalendarView의 monthHeaderBinder 설정
        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {}
        }

        // 월이 변경될 때 호출되는 리스너 설정
        calendarView.monthScrollListener = {
            updateMonthTitle(it)
        }
    }

    // 월 타이틀을 업데이트하는 메소드
    private fun updateMonthTitle(month: CalendarMonth) {
        val title =
            month.yearMonth.month.getDisplayName(
                TextStyle.FULL,
                Locale.getDefault()
            )

        monthTitle.text = title
    }

}

class EventsAdapter : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {
    var events: List<CalendarEvent> = emptyList()

    // 뷰 홀더를 생성하는 메소드
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_event_item_view, parent, false)
        return EventViewHolder(view)
    }

    // 뷰 홀더에 데이터를 바인딩하는 메소드
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    // 아이템 개수를 반환하는 메소드
    override fun getItemCount(): Int = events.size

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.eventTitleText)
        private val timeTextView: TextView = itemView.findViewById(R.id.eventTimeText)
        private val dateTextView: TextView = itemView.findViewById(R.id.eventDateText)

        // 이벤트 데이터를 뷰에 바인딩하는 메소드
        fun bind(event: CalendarEvent) {
//            titleTextView.text = event.title
//            timeTextView.text = event.time.format(DateTimeFormatter.ofPattern("HH:mm"))
//            dateTextView.text = event.time.toLocalDate().toString()
        }
    }
}


