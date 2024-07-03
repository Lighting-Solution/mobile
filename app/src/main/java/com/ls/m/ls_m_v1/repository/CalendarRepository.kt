package com.ls.m.ls_m_v1.repository

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.calendar.entity.CalendarEntity
import java.time.LocalDate

class CalendarRepository(context: Context){
    private val dbHelper = DatabaseHelper(context)

    // 받아온 데이터 삽입
    fun insertCalendarData(datas : List<CalendarEntity>){
        val db = dbHelper.writableDatabase

        for (data in datas){
            val value = ContentValues().apply {
                put("calendarTitle", data.calendarTitle)
                put("calendarCreateAt", data.calendarCreateAt)
                put("calendarContent", data.calendarContent)
                put("calendarStartAt", data.calendarStartAt)
                put("calendarEndAt", data.calendarEndAt)
            }
            db.insert(DatabaseHelper.CALENDER_TABLE, null, value)
        }
        db.close()
    }

    // 모든 캘린더 데이터 다 가져오기
    fun getAllCalendarData():List<CalendarEntity>{
        val datas = ArrayList<CalendarEntity>()
        val selectQuery = "SELECT * FROM ${DatabaseHelper.CALENDER_TABLE}"

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
                Log.d("CalendarRepository", "Event found: $data")
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return datas
    }

    // 특정 데이터 읽어옴
    fun getCalendarData(date: LocalDate): List<CalendarEntity> {
        val datas = ArrayList<CalendarEntity>()
        val dateString = date.toString()
        val selectQuery = "SELECT * FROM ${DatabaseHelper.CALENDER_TABLE} WHERE calendarStartAt <= ? AND calendarEndAt >= ?"

        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(dateString, dateString))

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
                Log.d("CalendarRepository", "Event found: $data")
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return datas
    }
}