package com.ls.m.ls_m_v1.databaseHelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "lighting_solution.db"
        const val CALENDER_TABLE = "calendar"

        const val DATABASE_VERSION = 1
    }

    //테이블 생성
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

    fun onDelete (tableName : String){
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $tableName")
        onCreate(db)
    }

}
