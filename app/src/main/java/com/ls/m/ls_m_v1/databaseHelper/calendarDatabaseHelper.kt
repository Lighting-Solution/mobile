package com.ls.m.ls_m_v1.databaseHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ls.m.ls_m_v1.entity.CalendarEntity

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "lighting_solution.db"
        const val TABLE_NAME = "calendar"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS calendar(
            calendarId INTEGER PRIMARY KEY AUTOINCREMENT,
            calendarTitle TEXT NOT NULL,
            calendarCreateAt TEXT NOT NULL,
            calendarContent TEXT,
            calendarStartAt TEXT NOT NULL,
            calendarEndAt TEXT NOT NULL
            );
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("Drop table if exists calendar")
        onCreate(db)
    }

}
class CalendarRepository(private val context : Context){
    private val dbHelper = DatabaseHelper(context)

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
            db.insert("calendar", null, value)
        }
        db.close()
    }

    fun getCalendarData() : List<CalendarEntity>{
        val datas = ArrayList<CalendarEntity>()
        val selectQuery = "SELECT * FROM ${DatabaseHelper.TABLE_NAME}"

        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()){
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
            }while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return datas
    }
}

