package com.ls.m.ls_m_v1.calendar

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.ls.m.ls_m_v1.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class SelectorDecorator(context: Context) : DayViewDecorator {
    private val drawable : Drawable = ContextCompat.getDrawable(context, R.drawable.custom_selector)!!

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return true
    }

    override fun decorate(view: DayViewFacade) {
        view.setSelectionDrawable(drawable)
    }

}
