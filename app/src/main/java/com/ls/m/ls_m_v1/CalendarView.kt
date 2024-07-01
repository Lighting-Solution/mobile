package com.ls.m.ls_m_v1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ls.m.ls_m_v1.calendar.CustomTitleFormatter
import com.ls.m.ls_m_v1.calendar.SaturdayDecorator
import com.ls.m.ls_m_v1.calendar.SelectorDecorator
import com.ls.m.ls_m_v1.calendar.SundayDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener

class CalendarView : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.activity_calender, container, false)

        // 캘린더 선언
        val calendar = view.findViewById<MaterialCalendarView>(R.id.calendarView)
        // 선택 포맷데이터
        val selectorDecorator = SelectorDecorator(requireContext())

        // 토요일, 일요일 날짜 컬러 변경
        val saturdayDecorator = SaturdayDecorator(requireContext())
        val sundayDecorator = SundayDecorator(requireContext())

        // 오늘 날짜가 자동으로 선택됨
        calendar.setSelectedDate(CalendarDay.today())
        
        // 날짜가 바뀔 때 마다 실행되는 메서드
        calendar.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->
            val selectedDate = "${date.day}/${date.month + 1}/${date.year}"
            Toast.makeText(context, "Selected date: $selectedDate", Toast.LENGTH_SHORT).show()

            calendar.invalidateDecorators()
        })

        // 토요일 일요일 선택 버튼
        calendar.addDecorators(saturdayDecorator, sundayDecorator, selectorDecorator)

        calendar.setTitleFormatter(CustomTitleFormatter())
        return view
    }
}

