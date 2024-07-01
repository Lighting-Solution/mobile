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

class SaturdayDecorator() : DayViewDecorator, Parcelable {

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
        return weekDay == Calendar.SATURDAY
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(ForegroundColorSpan(Color.BLUE))
    }

    companion object CREATOR : Parcelable.Creator<SaturdayDecorator> {
        override fun createFromParcel(parcel: Parcel): SaturdayDecorator {
            return SaturdayDecorator(parcel)
        }

        override fun newArray(size: Int): Array<SaturdayDecorator?> {
            return arrayOfNulls(size)
        }
    }
}