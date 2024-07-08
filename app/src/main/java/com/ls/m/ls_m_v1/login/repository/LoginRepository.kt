package com.ls.m.ls_m_v1.login.repository

import android.content.ContentValues
import android.content.Context
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.login.entity.LoginResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertTokenData(token: String, empId: Int) {
        val db = dbHelper.writableDatabase
        val value = ContentValues().apply {
            put("token", token)
            put("empId", empId)
        }
        db.insert(DatabaseHelper.DatabaseConstants.MY_EMP, null, value)
    }

    suspend fun getloginData() : LoginResponseDto{
        if (isDatabaseEmpty()) {
            throw IllegalStateException("Database is empty")
        }
        return withContext(Dispatchers.IO){
            val db = dbHelper.readableDatabase
            val cursor = db.query(
                DatabaseHelper.DatabaseConstants.MY_EMP,
                arrayOf("token", "empId"),
                null,
                null,
                null,
                null,
                null
            )

            val loginResponseDto = if(cursor.moveToFirst()){
                val token = cursor.getString(cursor.getColumnIndexOrThrow("token"))
                val empId = cursor.getInt(cursor.getColumnIndexOrThrow("empId"))
                LoginResponseDto(token, empId)
            }else {
                throw IllegalStateException("No user data found")
            }
            cursor.close()
            loginResponseDto
        }
    }

    // 테이블 삭제
    fun dropLoginTable(){
        val db = dbHelper.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseHelper.DatabaseConstants.MY_EMP}")
//        dbHelper.onCreate(db)
    }

    // 테이블 비어있는지 확인하는 메서드
    private fun isDatabaseEmpty(): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.DatabaseConstants.MY_EMP}", null)
        return if (cursor.moveToFirst()) {
            cursor.getInt(0) == 0
        } else {
            true
        }.also {
            cursor.close()
        }
    }




}