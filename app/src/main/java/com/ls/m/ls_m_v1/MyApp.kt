package com.ls.m.ls_m_v1

import android.app.Application
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper

class MyApp: Application() {
    private lateinit var dbHelper: DatabaseHelper
    override fun onCreate() {
        super.onCreate()
        dbHelper = DatabaseHelper(this)
    }
// 앱 종료시 삭제

    override fun onTerminate() {
        super.onTerminate()
        dbHelper.clearDatabase()
    }
}