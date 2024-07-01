package com.ls.m.ls_m_v1.calendar

import android.content.Context
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.Calendar

class SundayDecorator() : DayViewDecorator, Parcelable {

    private val calendar: Calendar = Calendar.getInstance()

    constructor(parcel: Parcel) : this()

    constructor(parcel: Context) : this()

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        day.copyTo(calendar)
        val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
        return weekDay == Calendar.SUNDAY
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(ForegroundColorSpan(Color.RED))
    }

    companion object CREATOR : Parcelable.Creator<SundayDecorator> {
        override fun createFromParcel(parcel: Parcel): SundayDecorator {
            return SundayDecorator(parcel)
        }

        override fun newArray(size: Int): Array<SundayDecorator?> {
            return arrayOfNulls(size)
        }
    }


}