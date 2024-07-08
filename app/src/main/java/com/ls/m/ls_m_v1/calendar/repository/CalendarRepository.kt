package com.ls.m.ls_m_v1.calendar.repository

import android.content.ContentValues
import android.content.Context
import com.ls.m.ls_m_v1.calendar.entity.CalendarEntity
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import java.time.LocalDate

class CalendarRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // insert Calendar
    fun insertCalendarData(datas: List<CalendarEntity>) {
        val db = dbHelper.writableDatabase
        for (data in datas) {
            val value = ContentValues().apply {
                put("calendarTitle", data.calendarTitle)
                put("calendarCreateAt", data.calendarCreateAt)
                put("calendarContent", data.calendarContent)
                put("calendarStartAt", data.calendarStartAt)
                put("calendarEndAt", data.calendarEndAt)
                put("allDay", data.allDay)
            }
            db.insert(DatabaseHelper.DatabaseConstants.CALENDAR_TABLE, null, value)
        }
        db.close()
    }

    fun getAllCalendar(): List<CalendarEntity> {
        val datas = mutableListOf<CalendarEntity>()
        val selectQuery = "select * from ${DatabaseHelper.DatabaseConstants.CALENDAR_TABLE}"

        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val data = CalendarEntity(
                    calendarId = cursor.getInt(cursor.getColumnIndexOrThrow("calendarId")),
                    calendarTitle = cursor.getString(cursor.getColumnIndexOrThrow("calendarTitle")),
                    calendarCreateAt = cursor.getString(cursor.getColumnIndexOrThrow("calendarCreateAt")),
                    calendarContent = cursor.getString(cursor.getColumnIndexOrThrow("calendarContent")),
                    calendarStartAt = cursor.getString(cursor.getColumnIndexOrThrow("calendarStartAt")),
                    calendarEndAt = cursor.getString(cursor.getColumnIndexOrThrow("calendarEndAt"))

                )
                datas.add(data)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return datas
    }
//    fun getCalendarData(date: LocalDate): List<CalendarEntity> {
//        val datas = mutableListOf<CalendarEntity>()
//        val dateString = date.toString()
//        val selectQuery =
//            "SELECT * FROM ${DatabaseHelper.DatabaseConstants}.CALENDAR_TABLE WHERE ? BETWEEN calendarStartAt AND calendarEndAt"
//
//        val db = dbHelper.readableDatabase
//        val cursor = db.rawQuery(selectQuery, arrayOf(dateString))
//
//        if (cursor.moveToFirst()) {
//            do {
//                val data = CalendarEntity(
//                    calendarId = cursor.getInt(cursor.getColumnIndexOrThrow("calendarId")),
//                    calendarTitle = cursor.getString(cursor.getColumnIndexOrThrow("calendarTitle")),
//                    calendarCreateAt = cursor.getString(cursor.getColumnIndexOrThrow("calendarCreateAt")),
//                    calendarContent = cursor.getString(cursor.getColumnIndexOrThrow("calendarContent")),
//                    calendarStartAt = cursor.getString(cursor.getColumnIndexOrThrow("calendarStartAt")),
//                    calendarEndAt = cursor.getString(cursor.getColumnIndexOrThrow("calendarEndAt"))
//                )
//                datas.add(data)
//            } while (cursor.moveToNext())
//        }
//        cursor.close()
//        db.close()
//        return datas
//    }

}