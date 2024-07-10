package com.ls.m.ls_m_v1.calendar

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ls.m.ls_m_v1.emp.SelectCalendarViewModel

class AddCalendarViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SelectCalendarViewModel::class.java)) {
            return SelectCalendarViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}