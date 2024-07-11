package com.ls.m.ls_m_v1.calendar.repository

import android.content.ContentValues
import android.content.Context
import com.ls.m.ls_m_v1.calendar.entity.CalendarDto
import com.ls.m.ls_m_v1.calendar.entity.CalendarEntity
import com.ls.m.ls_m_v1.calendar.entity.ParticipantEntity
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import java.time.LocalDate

class CalendarRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // insert Calendar
    fun insertCalendarInDatabase(calendarData: CalendarEntity) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("calendarId", calendarData.calendarId)
            put("calendarTitle", calendarData.calendarTitle)
            put("calendarCreateAt", calendarData.calendarCreateAt)
            put("calendarContent", calendarData.calendarContent)
            put("calendarStartAt", calendarData.calendarStartAt)
            put("calendarEndAt", calendarData.calendarEndAt)
            put("allDay", calendarData.allDay)
        }
        val whereClause = "calendarId = ?"
        val whereArgs = arrayOf(calendarData.calendarId.toString())
        db.update(DatabaseHelper.DatabaseConstants.CALENDAR_TABLE, values, whereClause, whereArgs)

        calendarData.participantEntity.forEach { participant ->
            val participantValues = ContentValues().apply {
                put("participantId", participant.participantId)
                put("calendarId", participant.calendarId)
                put("empId", participant.empId)
            }
            db.insert(DatabaseHelper.DatabaseConstants.PARTICIPANT_TABLE, null, participantValues)
        }
        db.close()
    }

    fun createCalendarInDatabase(calendarData: CalendarDto) {
        val db = dbHelper.writableDatabase
        try {
            // ContentValues 객체를 생성하여 값을 설정
            val values = ContentValues().apply {
                put("calendarTitle", calendarData.calendarTitle)
                put("calendarCreateAt", calendarData.calendarCreateAt)
                put("calendarContent", calendarData.calendarContent)
                put("calendarStartAt", calendarData.calendarStartAt)
                put("calendarEndAt", calendarData.calendarEndAt)
                put("allDay", calendarData.allDay)
            }

            // 새로운 calendar 행을 삽입하고, 생성된 calendarId를 가져옴
            val calendarId = db.insert(DatabaseHelper.DatabaseConstants.CALENDAR_TABLE, null, values)

            // participantEntity 리스트에 있는 각 참가자에 대해 데이터베이스에 새 행을 삽입
            calendarData.attendees.forEach { participant ->
                val participantValues = ContentValues().apply {
                    put("calendarId", calendarId)
                    put("empId", participant.id)
                }
                db.insert(DatabaseHelper.DatabaseConstants.PARTICIPANT_TABLE, null, participantValues)
            }
        } finally {
            db.close()
        }
    }


    fun insertParticipant(participantEntityList: List<ParticipantEntity>) {
        val db = dbHelper.writableDatabase
        for (data in participantEntityList) {
            val value = ContentValues().apply {
                put("participantId", data.participantId)
                put("calendarId", data.calendarId)
                put("empId", data.empId)
            }
            db.insert(DatabaseHelper.DatabaseConstants.PARTICIPANT_TABLE, null, value)
        }
        db.close()
    }

    fun getAllCalendar(): List<CalendarEntity> {
        val datas = mutableListOf<CalendarEntity>()
        val selectQuery = "SELECT * FROM ${DatabaseHelper.DatabaseConstants.CALENDAR_TABLE}"

        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val calendarId = cursor.getInt(cursor.getColumnIndexOrThrow("calendarId"))
                val participants = getParticipantsForCalendar(calendarId)
                val data = CalendarEntity(
                    calendarId = calendarId,
                    calendarTitle = cursor.getString(cursor.getColumnIndexOrThrow("calendarTitle")),
                    calendarCreateAt = cursor.getString(cursor.getColumnIndexOrThrow("calendarCreateAt")),
                    calendarContent = cursor.getString(cursor.getColumnIndexOrThrow("calendarContent")),
                    calendarStartAt = cursor.getString(cursor.getColumnIndexOrThrow("calendarStartAt")),
                    calendarEndAt = cursor.getString(cursor.getColumnIndexOrThrow("calendarEndAt")),
                    participantEntity = participants,
                    allDay = cursor.getInt(cursor.getColumnIndexOrThrow("allDay"))
                )
                datas.add(data)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return datas
    }

    fun getParticipantsForCalendar(calendarId: Int): List<ParticipantEntity> {
        val participants = mutableListOf<ParticipantEntity>()
        val selectQuery =
            "SELECT * FROM ${DatabaseHelper.DatabaseConstants.PARTICIPANT_TABLE} WHERE calendarId = ?"

        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(calendarId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val participant = ParticipantEntity(
                    participantId = cursor.getInt(cursor.getColumnIndexOrThrow("participantId")),
                    calendarId = cursor.getInt(cursor.getColumnIndexOrThrow("calendarId")),
                    empId = cursor.getInt(cursor.getColumnIndexOrThrow("empId"))
                )
                participants.add(participant)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return participants
    }

    fun forRefresh() {
        val db = dbHelper.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseHelper.DatabaseConstants.CALENDAR_TABLE}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseHelper.DatabaseConstants.PARTICIPANT_TABLE}")
        dbHelper.onCreate(db)
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