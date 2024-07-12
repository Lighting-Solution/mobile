package com.ls.m.ls_m_v1.calendar.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.ls.m.ls_m_v1.calendar.dto.CalendarEntityDTO
import com.ls.m.ls_m_v1.calendar.dto.CalendarEvent
import com.ls.m.ls_m_v1.calendar.entity.CalendarDto
import com.ls.m.ls_m_v1.calendar.entity.CalendarEntity
import com.ls.m.ls_m_v1.calendar.entity.ParticipantEntity
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper

class CalendarRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertCalendarInDatabase(calendarData: CalendarEntity) {
        val db = dbHelper.writableDatabase
        try {
            val values = ContentValues().apply {
                put("calendarId", calendarData.calendarId.toLong())
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
                    put("calendarId", participant.calendarId.toLong())
                    put("empId", participant.empId.toLong())
                }
                db.insert(DatabaseHelper.DatabaseConstants.PARTICIPANT_TABLE, null, participantValues)
            }
        } finally {
            db.close()
        }
    }

    fun createCalendarInDatabase(calendarData: CalendarDto) {
        val db = dbHelper.writableDatabase
        try {
            val values = ContentValues().apply {
                put("calendarTitle", calendarData.calendarTitle)
                put("calendarCreateAt", calendarData.calendarCreateAt)
                put("calendarContent", calendarData.calendarContent)
                put("calendarStartAt", calendarData.calendarStartAt)
                put("calendarEndAt", calendarData.calendarEndAt)
                put("allDay", calendarData.allDay)
            }
            val calendarId = db.insert(DatabaseHelper.DatabaseConstants.CALENDAR_TABLE, null, values)

            calendarData.attendees.forEach { participant ->
                val participantValues = ContentValues().apply {
                    put("calendarId", calendarId)
                    put("empId", participant.id.toLong())
                }
                db.insert(DatabaseHelper.DatabaseConstants.PARTICIPANT_TABLE, null, participantValues)
            }
        } finally {
            db.close()
        }
    }

    fun insertParticipant(participantEntityList: List<ParticipantEntity>) {
        val db = dbHelper.writableDatabase
        try {
            for (data in participantEntityList) {
                val value = ContentValues().apply {
                    put("participantId", data.participantId.toLong())
                    put("calendarId", data.calendarId.toLong())
                    put("empId", data.empId.toLong())
                }
                db.insert(DatabaseHelper.DatabaseConstants.PARTICIPANT_TABLE, null, value)
            }
        } finally {
            db.close()
        }
    }

    fun getAllCalendar(): List<CalendarEntityDTO> {
        val datas = mutableListOf<CalendarEntityDTO>()
        val selectQuery = "SELECT * FROM ${DatabaseHelper.DatabaseConstants.CALENDAR_TABLE}"
        val db = dbHelper.readableDatabase

        try {
            val cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    val calendarId = cursor.getInt(cursor.getColumnIndexOrThrow("calendarId"))
                    val data = CalendarEntityDTO(
                        calendarId = calendarId,
                        calendarTitle = cursor.getString(cursor.getColumnIndexOrThrow("calendarTitle")),
                        calendarCreateAt = cursor.getString(cursor.getColumnIndexOrThrow("calendarCreateAt")),
                        calendarContent = cursor.getString(cursor.getColumnIndexOrThrow("calendarContent")),
                        calendarStartAt = cursor.getString(cursor.getColumnIndexOrThrow("calendarStartAt")),
                        calendarEndAt = cursor.getString(cursor.getColumnIndexOrThrow("calendarEndAt")),
                        allDay = cursor.getInt(cursor.getColumnIndexOrThrow("allDay"))
                    )
                    datas.add(data)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } finally {
            db.close()
        }
        return datas
    }

    fun getParticipantsForCalendar(calendarId: Int): List<ParticipantEntity> {
        val participants = mutableListOf<ParticipantEntity>()
        val selectQuery = "SELECT * FROM ${DatabaseHelper.DatabaseConstants.PARTICIPANT_TABLE} WHERE calendarId = ?"
        val db = dbHelper.readableDatabase

        try {
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
        } finally {
            db.close()
        }
        return participants
    }

    fun forRefresh() {
        val db = dbHelper.writableDatabase
        try {
            db.execSQL("DROP TABLE IF EXISTS ${DatabaseHelper.DatabaseConstants.CALENDAR_TABLE}")
            db.execSQL("DROP TABLE IF EXISTS ${DatabaseHelper.DatabaseConstants.PARTICIPANT_TABLE}")
            dbHelper.onCreate(db)
        } finally {
            db.close()
        }
    }
}
