package com.ls.m.ls_m_v1.databaseHelper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
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
            CREATE TABLE IF NOT EXISTS $DatabaseConstants.MY_EMP(
                empId INTEGER PRIMARY KEY,
                token TEXT NOT NULL,
            );
        """.trimIndent()

        val createCalendarTable = """
            CREATE TABLE IF NOT EXISTS $DatabaseConstants.CALENDAR_TABLE(
                calendarId INTEGER PRIMARY KEY AUTOINCREMENT,
                calendarTitle TEXT NOT NULL,
                calendarCreateAt TEXT NOT NULL,
                calendarContent TEXT,
                calendarStartAt TEXT NOT NULL,
                calendarEndAt TEXT NOT NULL
            );
        """.trimIndent()

        val createEmpTable = """
            CREATE TABLE IF NOT EXISTS $DatabaseConstants.EMP_TABLE(
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
                departmentId INTEGER,
            );
        """.trimIndent()
//                FOREIGN KEY (companyId) REFERENCES $DatabaseConstants.COMPANY_TABLE(companyId),
//                FOREIGN KEY (positionId) REFERENCES $DatabaseConstants.POSITION_TABLE(positionId),
//                FOREIGN KEY (departmentId) REFERENCES $DatabaseConstants.DEPARTMENT_TABLE(departmentId)

        val createPositionTable = """
            CREATE TABLE IF NOT EXISTS $DatabaseConstants.POSITION_TABLE(
                positionId INTEGER PRIMARY KEY,
                positionName TEXT NOT NULL
            );
        """.trimIndent()

        val createDepartmentTable = """
            CREATE TABLE IF NOT EXISTS $DatabaseConstants.DEPARTMENT_TABLE(
                departmentId INTEGER PRIMARY KEY,
                departmentName TEXT NOT NULL
            );
        """.trimIndent()

        val createCompanyTable = """
            CREATE TABLE IF NOT EXISTS $DatabaseConstants.COMPANY_TABLE(
                companyId INTEGER PRIMARY KEY,
                companyName TEXT NOT NULL,
                companyAddress TEXT,
                companyURL TEXT,
                companyNumber TEXT,
                companyFax TEXT
            );
        """.trimIndent()


        val createPersonalContactTable = """
            CREATE TABLE IF NOT EXISTS $DatabaseConstants.PERSONAL_CONTACT_TABLE(
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
                empId INTEGER,
            );
        """.trimIndent()
//                FOREIGN KEY (companyId) REFERENCES $DatabaseConstants.COMPANY_TABLE(companyId),
//                FOREIGN KEY (empId) REFERENCES $DatabaseConstants.EMP_TABLE(empId)

        val createPersonalGroupTable = """
            CREATE TABLE IF NOT EXISTS $DatabaseConstants.PERSONAL_GROUP_TABLE(
                personalGroupId INTEGER PRIMARY KEY AUTOINCREMENT,
                empId INTEGER,
                personalGroupName TEXT NOT NULL,
            );
        """.trimIndent()
//                FOREIGN KEY (empId) REFERENCES $DatabaseConstants.EMP_TABLE(empId)

        val createContactGroupTable = """
            CREATE TABLE IF NOT EXISTS $DatabaseConstants.CONTACT_GROUP_TABLE(
                contactGroupId INTEGER PRIMARY KEY AUTOINCREMENT,
                personalContactId INTEGER,
                personalGroupId INTEGER,
            );
        """.trimIndent()
//                FOREIGN KEY (personalContactId) REFERENCES $DatabaseConstants.PERSONAL_CONTACT_TABLE(personalContactId),
//                FOREIGN KEY (personalGroupId) REFERENCES $DatabaseConstants.PERSONAL_GROUP_TABLE(personalGroupId)

        val createDigitalApprovalTable = """
           CREATE TABLE IF NOT EXISTS $DatabaseConstants.APPROVAL_TABLE (
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
                ceoRejectAt DATETIME,
           );
        """.trimIndent()
//                FOREIGN KEY (empId) REFERENCES $DatabaseConstants.EMP_TABLE(empId)

        // company에 기본 데이터 저장
        val insertCompanyDefaultData = """
            INSERT INTO ${DatabaseConstants.COMPANY_TABLE} (company_id, name, address, url, number, 
            fax) VALUES(
            1, '라이팅 솔루션', '서울특별시 강남구 테헤란로 123', 
            'http://www.lightingsolution.co.kr', '1010-1010', '02-0101-0101'
            );
        """.trimIndent()

        db?.execSQL(createRepo)
        db?.execSQL(createCalendarTable)
        db?.execSQL(createEmpTable)
        db?.execSQL(createPositionTable)
        db?.execSQL(createDepartmentTable)
        db?.execSQL(createCompanyTable)
        // company 기본 데이터 넣기
        db?.execSQL(insertCompanyDefaultData)
        db?.execSQL(createPersonalContactTable)
        db?.execSQL(createPersonalGroupTable)
        db?.execSQL(createContactGroupTable)
        db?.execSQL(createDigitalApprovalTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.CALENDAR_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.EMP_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.POSITION_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.DEPARTMENT_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.COMPANY_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.PERSONAL_CONTACT_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.PERSONAL_GROUP_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.CONTACT_GROUP_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.APPROVAL_TABLE")
        onCreate(db)
    }

    fun onDelete(tableName: String) {
        val db = this.writableDatabase
        db.execSQL("DELETE TABLE $tableName")
        onCreate(db)
    }

    fun insertCalendarData(datas: List<CalendarEntity>) {
        val db = this.writableDatabase
        for (data in datas) {
            val value = ContentValues().apply {
                put("calendarTitle", data.calendarTitle)
                put("calendarCreateAt", data.calendarCreateAt)
                put("calendarContent", data.calendarContent)
                put("calendarStartAt", data.calendarStartAt)
                put("calendarEndAt", data.calendarEndAt)
            }
            db.insert(DatabaseConstants.CALENDAR_TABLE, null, value)
        }
        db.close()
    }

    fun getAllCalendar(): List<CalendarEntity> {
        val datas = mutableListOf<CalendarEntity>()
        val selectQuery = "select * from ${DatabaseConstants.CALENDAR_TABLE}"

        val db = this.readableDatabase
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

    fun getCalendarData(date: LocalDate): List<CalendarEntity> {
        val datas = mutableListOf<CalendarEntity>()
        val dateString = date.toString()
        val selectQuery =
            "SELECT * FROM $DatabaseConstants.CALENDAR_TABLE WHERE ? BETWEEN calendarStartAt AND calendarEndAt"

        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(dateString))

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

    fun getApprovalWithEMPDrafter(empId: Int): List<Pair<ApprovalEntity, ApprovalEmpDTO>> {
        val ApprovalWithEMP = mutableListOf<Pair<ApprovalEntity, ApprovalEmpDTO>>()
        val db = this.readableDatabase
        /*
                // 전자결재 데이터와 emp데이터를 조인하는 쿼리
                val selectQuery = """
                    SELECT
                        da.pdocument_id, da.drafter_id, da.name AS document_name, da.pdocumentPath,
                        da.dBoxType, da.drafter_status, da.manager_status, da.ceo_status,
                        da.created_at, da.approval_at,
                        e.empId, e.empName,
                        p.positionName AS position,
                        d.departmentName AS department
                    FROM digital_approval da
                    JOIN $EMP_TABLE e ON da.drafter_id = e.empId
                    JOIN $POSITION_TABLE p ON e.positionId = p.positionId
                    JOIN $DEPARTMENT_TABLE d ON e.departmentId = d.departmentId
                """.trimIndent()*/

        // SQL 쿼리: empId가 기안자이거나, 기안자의 부서의 부서장이거나, 대표이사일 경우에만 데이터를 가져옵니다.
        val selectQuery = """
            SELECT 
                da.*, e.*, p.name AS $DatabaseConstants.POSITION_TABLE, d.name AS $DatabaseConstants.DEPARTMENT_TABLE
            FROM digital_approval da
            JOIN $DatabaseConstants.EMP_TABLE e ON da.drafter_id = e.empId
            JOIN $DatabaseConstants.POSITION_TABLE p ON e.positionId = p.position_id
            JOIN $DatabaseConstants.DEPARTMENT_TABLE d ON e.departmentId = d.department_id
            WHERE da.drafter_id = ? 
            OR (da.drafter_id IN (SELECT empId FROM $DatabaseConstants.EMP_TABLE WHERE departmentId = (SELECT departmentId FROM $DatabaseConstants.EMP_TABLE WHERE empId = ?) AND positionId = 2))
            OR (? IN (SELECT empId FROM $DatabaseConstants.EMP_TABLE WHERE positionId = 1))
        """.trimIndent()

        val cursor =
            db.rawQuery(selectQuery, arrayOf(empId.toString(), empId.toString(), empId.toString()))

        with(cursor) {
            while (moveToNext()) {
                val pdocumentId = getInt(getColumnIndexOrThrow("digitalApprovalId"))
                val drafterId = getInt(getColumnIndexOrThrow("drafterId"))
                val documentName = getString(getColumnIndexOrThrow("digitalApprovalName"))
                val pdocumentPath = getString(getColumnIndexOrThrow("digitalApprovalPath"))
                val dBoxType = getInt(getColumnIndexOrThrow("digitalApprovalType")) == 1
                val drafterStatus = getInt(getColumnIndexOrThrow("drafterStatus")) == 1
                val managerStatus = getInt(getColumnIndexOrThrow("managerStatus")) == 1
                val ceoStatus = getInt(getColumnIndexOrThrow("ceoStatus")) == 1
                val createdAt = getString(getColumnIndexOrThrow("digitalApprovalCreateAt"))
                val approvalAt = getString(getColumnIndexOrThrow("digitalApprovalAt"))
                val managerRejectAt = getString(getColumnIndexOrThrow("managerRejectAt"))
                val ceoRejectAt = getString(getColumnIndexOrThrow("ceoRejectAt"))

                val approvalEntity = ApprovalEntity(
                    digitalApprovalId = pdocumentId,
                    drafterId = drafterId,
                    digitalApprovalName = documentName,
                    digitalApprovalPath = pdocumentPath,
                    digitalApprovalType = dBoxType,
                    drafterStatus = drafterStatus,
                    managerStatus = managerStatus,
                    ceoStatus = ceoStatus,
                    empId = drafterId,
                    digitalApprovalCreateAt = LocalDateTime.parse(createdAt),
                    digitalApprovalAt = LocalDateTime.parse(approvalAt),
                    managerRejectAt = LocalDateTime.parse(managerRejectAt),
                    ceoRejectAt = LocalDateTime.parse(ceoRejectAt)
                )

                val empId = getInt(getColumnIndexOrThrow("empId"))
                val empName = getString(getColumnIndexOrThrow("empName"))
                val position = getString(getColumnIndexOrThrow("position"))
                val department = getString(getColumnIndexOrThrow("department"))

                val approvalEmpDTO = ApprovalEmpDTO(
                    empId = empId,
                    empName = empName,
                    position = position,
                    department = department
                )

                ApprovalWithEMP.add(Pair(approvalEntity, approvalEmpDTO))
            }
        }
        cursor.close()
        return ApprovalWithEMP
    }

    fun clearDatabase() {
        val db = this.writableDatabase
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.CALENDAR_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.EMP_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.POSITION_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.DEPARTMENT_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.PERSONAL_CONTACT_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.PERSONAL_GROUP_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.CONTACT_GROUP_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.APPROVAL_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseConstants.MY_EMP")
        // 1번 데이터를 제외하고 삭제
        db?.execSQL("DELETE FROM $DatabaseConstants.COMPANY_TABLE WHERE id > 1")
    }

    fun getCompany(companyId: Int): CompanyDTO {
        val db = this.readableDatabase
        val cursor = db.query(
            DatabaseHelper.DatabaseConstants.COMPANY_TABLE,
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
