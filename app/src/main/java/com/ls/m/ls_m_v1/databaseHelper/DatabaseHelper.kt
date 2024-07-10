package com.ls.m.ls_m_v1.databaseHelper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.ls.m.ls_m_v1.approval.entity.ApprovalEmpDTO
import com.ls.m.ls_m_v1.approval.entity.ApprovalEntity
import com.ls.m.ls_m_v1.calendar.entity.CalendarEntity
import com.ls.m.ls_m_v1.emp.entity.DepartmentDTO
import com.ls.m.ls_m_v1.emp.entity.EmpDTO
import com.ls.m.ls_m_v1.emp.entity.PositionDTO
import com.ls.m.ls_m_v1.login.entity.LoginResponseDto
import com.ls.m.ls_m_v1.p_contect.entity.*
import java.time.LocalDate
import java.time.LocalDateTime

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(
        context,
        DatabaseConstants.DATABASE_NAME,
        null,
        DatabaseConstants.DATABASE_VERSION
    ) {

    object DatabaseConstants {
        const val DATABASE_NAME = "lighting_solution.db"
        const val DATABASE_VERSION = 1

        const val CALENDAR_TABLE = "calendar"
        const val EMP_TABLE = "emp"
        const val POSITION_TABLE = "position"
        const val DEPARTMENT_TABLE = "department"
        const val COMPANY_TABLE = "company"
        const val PERSONAL_CONTACT_TABLE = "personal_contact"
        const val PERSONAL_GROUP_TABLE = "personal_group"
        const val CONTACT_GROUP_TABLE = "contact_group"
        const val APPROVAL_TABLE = "digital_approval"
        const val MY_EMP = "repo"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createRepo = """
            CREATE TABLE IF NOT EXISTS ${DatabaseConstants.MY_EMP} (
                empId INTEGER PRIMARY KEY,
                token TEXT NOT NULL,
                positionId INTEGER
            );
        """.trimIndent()

        val createCalendarTable = """
            CREATE TABLE IF NOT EXISTS ${DatabaseConstants.CALENDAR_TABLE} (
                calendarId INTEGER PRIMARY KEY AUTOINCREMENT,
                calendarTitle TEXT NOT NULL,
                calendarCreateAt TEXT NOT NULL,
                calendarContent TEXT,
                calendarStartAt TEXT NOT NULL,
                calendarEndAt TEXT NOT NULL,
                allDay INTEGER
            );
        """.trimIndent()

        val createEmpTable = """
            CREATE TABLE IF NOT EXISTS ${DatabaseConstants.EMP_TABLE} (
                empId INTEGER PRIMARY KEY,
                empName TEXT NOT NULL,
                empEmail TEXT NOT NULL,
                empMP TEXT,
                empMemo TEXT,
                empHP TEXT,
                empHomeAddress TEXT,
                empHomeFax TEXT,
                empBirthday TEXT,
                companyId INTEGER,
                positionId INTEGER,
                departmentId INTEGER
            );
        """.trimIndent()

        val createPositionTable = """
            CREATE TABLE IF NOT EXISTS ${DatabaseConstants.POSITION_TABLE} (
                positionId INTEGER PRIMARY KEY,
                positionName TEXT NOT NULL
            );
        """.trimIndent()

        val createDepartmentTable = """
            CREATE TABLE IF NOT EXISTS ${DatabaseConstants.DEPARTMENT_TABLE} (
                departmentId INTEGER PRIMARY KEY,
                departmentName TEXT NOT NULL
            );
        """.trimIndent()

        val createCompanyTable = """
            CREATE TABLE IF NOT EXISTS ${DatabaseConstants.COMPANY_TABLE} (
                companyId INTEGER PRIMARY KEY,
                companyName TEXT NOT NULL,
                companyAddress TEXT,
                companyURL TEXT,
                companyNumber TEXT,
                companyFax TEXT
            );
        """.trimIndent()

        val createPersonalContactTable = """
            CREATE TABLE IF NOT EXISTS ${DatabaseConstants.PERSONAL_CONTACT_TABLE} (
                personalContactId INTEGER PRIMARY KEY AUTOINCREMENT,
                positionName TEXT NOT NULL,
                departmentName TEXT NOT NULL,
                personalContactName TEXT NOT NULL,
                personalContactNickName TEXT,
                personalContactEmail TEXT NOT NULL,
                personalContactMP TEXT,
                personalContactMemo TEXT,
                personalContactBirthday TEXT,
                companyId INTEGER,
                empId INTEGER
            );
        """.trimIndent()

        val createPersonalGroupTable = """
            CREATE TABLE IF NOT EXISTS ${DatabaseConstants.PERSONAL_GROUP_TABLE} (
                personalGroupId INTEGER PRIMARY KEY AUTOINCREMENT,
                empId INTEGER,
                personalGroupName TEXT NOT NULL
            );
        """.trimIndent()

        val createContactGroupTable = """
            CREATE TABLE IF NOT EXISTS ${DatabaseConstants.CONTACT_GROUP_TABLE} (
                contactGroupId INTEGER PRIMARY KEY AUTOINCREMENT,
                personalContactId INTEGER,
                personalGroupId INTEGER
            );
        """.trimIndent()

        val createDigitalApprovalTable = """
            CREATE TABLE IF NOT EXISTS ${DatabaseConstants.APPROVAL_TABLE} (
                digitalApprovalId INTEGER PRIMARY KEY,
                empId INTEGER,
                drafterId INTEGER,
                digitalApprovalName TEXT,
                digitalApprovalPath TEXT,
                digitalApprovalType INTEGER,
                drafterStatus INTEGER,
                managerStatus INTEGER,
                ceoStatus INTEGER,
                digitalApprovalCreateAt DATETIME,
                digitalApprovalAt DATETIME,
                managerRejectAt DATETIME,
                ceoRejectAt DATETIME
            );
        """.trimIndent()

        val insertCompanyDefaultData = """
            INSERT INTO ${DatabaseConstants.COMPANY_TABLE} (
                companyId, companyName, companyAddress, companyURL, companyNumber, companyFax
            ) VALUES (
                1, '라이팅 솔루션', '서울특별시 강남구 테헤란로 123', 
                'http://www.lightingsolution.co.kr', '1010-1010', '02-0101-0101'
            );
        """.trimIndent()
        try {
            db?.execSQL(createRepo)
            db?.execSQL(createCalendarTable)
            db?.execSQL(createEmpTable)
            db?.execSQL(createPositionTable)
            db?.execSQL(createDepartmentTable)
            db?.execSQL(createCompanyTable)
            db?.execSQL(insertCompanyDefaultData)
            db?.execSQL(createPersonalContactTable)
            db?.execSQL(createPersonalGroupTable)
            db?.execSQL(createContactGroupTable)
            db?.execSQL(createDigitalApprovalTable)
        }catch (e : Exception){
            Log.e("DatabaseHelper", "Error while creating database tables: ${e.message}")
        }

    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.CALENDAR_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.EMP_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.POSITION_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.DEPARTMENT_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.COMPANY_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.PERSONAL_CONTACT_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.PERSONAL_GROUP_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.CONTACT_GROUP_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.APPROVAL_TABLE}")
        onCreate(db)
    }

    fun onDelete(tableName: String) {
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $tableName")
        onCreate(db)
    }



    fun getPosition(positionId: Int): PositionDTO {
        val db = this.readableDatabase
        val cursor = db.query(
            DatabaseConstants.POSITION_TABLE,
            null,
            "positionId=?",
            arrayOf(positionId.toString()),
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            PositionDTO(
                positionId = cursor.getInt(cursor.getColumnIndexOrThrow("positionId")),
                positionName = cursor.getString(cursor.getColumnIndexOrThrow("positionName"))
            )
        } else {
            PositionDTO(0, "")
        }.also {
            cursor.close()
        }
    }

    fun getDepartment(departmentId: Int): DepartmentDTO {
        val db = this.readableDatabase
        val cursor = db.query(
            DatabaseConstants.DEPARTMENT_TABLE,
            null,
            "departmentId=?",
            arrayOf(departmentId.toString()),
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            DepartmentDTO(
                departmentId = cursor.getInt(cursor.getColumnIndexOrThrow("departmentId")),
                departmentName = cursor.getString(cursor.getColumnIndexOrThrow("departmentName"))
            )
        } else {
            DepartmentDTO(0, "")
        }.also {
            cursor.close()
        }
    }

    fun clearDatabase() {
        val db = this.writableDatabase
        try {
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.CALENDAR_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.EMP_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.POSITION_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.DEPARTMENT_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.PERSONAL_CONTACT_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.PERSONAL_GROUP_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.CONTACT_GROUP_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.APPROVAL_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseConstants.MY_EMP}")
        // 1번 데이터를 제외하고 삭제
        db?.execSQL("DELETE FROM $DatabaseConstants.COMPANY_TABLE WHERE id > 1")
        }catch (e: Exception) {
            Log.e("DatabaseHelper", "Error while clearing database: ${e.message}")
        }
    }

    fun getCompany(companyId: Int): CompanyDTO {
        val db = this.readableDatabase
        val cursor = db.query(
            DatabaseConstants.COMPANY_TABLE,
            null,
            "companyId=?",
            arrayOf(companyId.toString()),
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            CompanyDTO(
                companyId = cursor.getInt(cursor.getColumnIndexOrThrow("companyId")),
                companyName = cursor.getString(cursor.getColumnIndexOrThrow("companyName")),
                companyAddress = cursor.getString(cursor.getColumnIndexOrThrow("companyAddress")),
                companyURL = cursor.getString(cursor.getColumnIndexOrThrow("companyURL")),
                companyNumber = cursor.getString(cursor.getColumnIndexOrThrow("companyNumber")),
                companyFax = cursor.getString(cursor.getColumnIndexOrThrow("companyFax"))
            )
        } else {
            CompanyDTO(0, "", "", "", "", "")
        }.also {
            cursor.close()
        }
    }
}
