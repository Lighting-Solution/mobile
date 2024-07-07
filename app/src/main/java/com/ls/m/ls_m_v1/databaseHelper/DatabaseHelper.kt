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
import com.ls.m.ls_m_v1.p_contect.entity.*
import java.time.LocalDate
import java.time.LocalDateTime

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
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
            CREATE TABLE IF NOT EXISTE $MY_EMP(
                empId INTEGER PRIMARY KEY
            );
        """.trimIndent()

        val createCalendarTable = """
            CREATE TABLE IF NOT EXISTS $CALENDAR_TABLE(
                calendarId INTEGER PRIMARY KEY AUTOINCREMENT,
                calendarTitle TEXT NOT NULL,
                calendarCreateAt TEXT NOT NULL,
                calendarContent TEXT,
                calendarStartAt TEXT NOT NULL,
                calendarEndAt TEXT NOT NULL
            );
        """.trimIndent()

        val createEmpTable = """
            CREATE TABLE IF NOT EXISTS $EMP_TABLE(
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
                FOREIGN KEY (companyId) REFERENCES $COMPANY_TABLE(companyId),
                FOREIGN KEY (positionId) REFERENCES $POSITION_TABLE(positionId),
                FOREIGN KEY (departmentId) REFERENCES $DEPARTMENT_TABLE(departmentId)
            );
        """.trimIndent()

        val createPositionTable = """
            CREATE TABLE IF NOT EXISTS $POSITION_TABLE(
                positionId INTEGER PRIMARY KEY,
                positionName TEXT NOT NULL
            );
        """.trimIndent()

        val createDepartmentTable = """
            CREATE TABLE IF NOT EXISTS $DEPARTMENT_TABLE(
                departmentId INTEGER PRIMARY KEY,
                departmentName TEXT NOT NULL
            );
        """.trimIndent()

        val createCompanyTable = """
            CREATE TABLE IF NOT EXISTS $COMPANY_TABLE(
                companyId INTEGER PRIMARY KEY,
                companyName TEXT NOT NULL,
                companyAddress TEXT,
                companyURL TEXT,
                companyNumber TEXT,
                companyFax TEXT
            );
        """.trimIndent()

        val createPersonalContactTable = """
            CREATE TABLE IF NOT EXISTS $PERSONAL_CONTACT_TABLE(
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
                FOREIGN KEY (companyId) REFERENCES $COMPANY_TABLE(companyId),
                FOREIGN KEY (empId) REFERENCES $EMP_TABLE(empId)
            );
        """.trimIndent()

        val createPersonalGroupTable = """
            CREATE TABLE IF NOT EXISTS $PERSONAL_GROUP_TABLE(
                personalGroupId INTEGER PRIMARY KEY AUTOINCREMENT,
                empId INTEGER,
                personalGroupName TEXT NOT NULL,
                FOREIGN KEY (empId) REFERENCES $EMP_TABLE(empId)
            );
        """.trimIndent()

        val createContactGroupTable = """
            CREATE TABLE IF NOT EXISTS $CONTACT_GROUP_TABLE(
                contactGroupId INTEGER PRIMARY KEY AUTOINCREMENT,
                personalContactId INTEGER,
                personalGroupId INTEGER,
                FOREIGN KEY (personalContactId) REFERENCES $PERSONAL_CONTACT_TABLE(personalContactId),
                FOREIGN KEY (personalGroupId) REFERENCES $PERSONAL_GROUP_TABLE(personalGroupId)
            );
        """.trimIndent()

        val createDigitalApprovalTable = """
           CREATE TABLE IF NOT EXISTS $APPROVAL_TABLE (
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
                FOREIGN KEY (empId) REFERENCES $EMP_TABLE(empId)
           );
        """.trimIndent()

        db?.execSQL(createRepo)
        db?.execSQL(createCalendarTable)
        db?.execSQL(createEmpTable)
        db?.execSQL(createPositionTable)
        db?.execSQL(createDepartmentTable)
        db?.execSQL(createCompanyTable)
        db?.execSQL(createPersonalContactTable)
        db?.execSQL(createPersonalGroupTable)
        db?.execSQL(createContactGroupTable)
        db?.execSQL(createDigitalApprovalTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $CALENDAR_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $EMP_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $POSITION_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DEPARTMENT_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $COMPANY_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $PERSONAL_CONTACT_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $PERSONAL_GROUP_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $CONTACT_GROUP_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $APPROVAL_TABLE")
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
            db.insert(DatabaseHelper.CALENDAR_TABLE, null, value)
        }
        db.close()
    }

    // Insert Emp
    fun insertEmp(emp: EmpDTO) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("empId", emp.empId)
            put("empName", emp.empName)
            put("empEmail", emp.empEmail)
            put("empMP", emp.empMP)
            put("empMemo", emp.empMemo)
            put("empHP", emp.empHP)
            put("empHomeAddress", emp.empHomeAddress)
            put("empHomeFax", emp.empHomeFax)
            put("empBirthday", emp.empBirthday.toString())
            put("companyId", emp.company.companyId)
            put("positionId", emp.position.positionId)
            put("departmentId", emp.department.departmentId)
        }
        db.insert(EMP_TABLE, null, values)
    }

    // Insert Personal Contact
    fun insertPersonalContact(contact: PersonalContactDTO) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("personalContactId", contact.personalContactId)
            put("positionName", contact.positionName)
            put("departmentName", contact.departmentName)
            put("personalContactName", contact.personalContactName)
            put("personalContactNickName", contact.personalContactNickName)
            put("personalContactEmail", contact.personalContactEmail)
            put("personalContactMP", contact.personalContactMP)
            put("personalContactMemo", contact.personalContactMemo)
            put("personalContactBirthday", contact.personalContactBirthday.toString())
            put("companyId", contact.company.companyId)
            put("empId", contact.empId)
        }
        db.insert(PERSONAL_CONTACT_TABLE, null, values)
    }

    // Insert Company
    fun insertCompany(company: CompanyDTO): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("companyName", company.companyName)
            put("companyAddress", company.companyAddress)
            put("companyURL", company.companyURL)
            put("companyNumber", company.companyNumber)
            put("companyFax", company.companyFax)
        }
        val id = db.insert(COMPANY_TABLE, null, values)
        db.close()
        return id
    }

    fun getAllCalendar(): List<CalendarEntity> {
        val datas = mutableListOf<CalendarEntity>()
        val selectQuery = "select * from ${DatabaseHelper.CALENDAR_TABLE}"

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
            "SELECT * FROM $CALENDAR_TABLE WHERE ? BETWEEN calendarStartAt AND calendarEndAt"

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

    // Get All Emps
    fun getAllEmps(): List<EmpDTO> {
        val emps = mutableListOf<EmpDTO>()
        val db = this.readableDatabase
        val cursor: Cursor = db.query(EMP_TABLE, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val emp = EmpDTO(
                    empId = cursor.getInt(cursor.getColumnIndexOrThrow("empId")),
                    empName = cursor.getString(cursor.getColumnIndexOrThrow("empName")),
                    empEmail = cursor.getString(cursor.getColumnIndexOrThrow("empEmail")),
                    empMP = cursor.getString(cursor.getColumnIndexOrThrow("empMP")),
                    empMemo = cursor.getString(cursor.getColumnIndexOrThrow("empMemo")),
                    empHP = cursor.getString(cursor.getColumnIndexOrThrow("empHP")),
                    empHomeAddress = cursor.getString(cursor.getColumnIndexOrThrow("empHomeAddress")),
                    empHomeFax = cursor.getString(cursor.getColumnIndexOrThrow("empHomeFax")),
                    empBirthday = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow("empBirthday"))),
                    company = getCompany(cursor.getInt(cursor.getColumnIndexOrThrow("companyId"))),
                    position = getPosition(cursor.getInt(cursor.getColumnIndexOrThrow("positionId"))),
                    department = getDepartment(cursor.getInt(cursor.getColumnIndexOrThrow("departmentId")))
                )
                emps.add(emp)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return emps
    }

    // Get All Personal Contacts
    fun getAllPersonalContacts(): List<PersonalContactDTO> {
        val contacts = mutableListOf<PersonalContactDTO>()
        val db = this.readableDatabase
        val cursor: Cursor = db.query(PERSONAL_CONTACT_TABLE, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val contact = PersonalContactDTO(
                    personalContactId = cursor.getInt(cursor.getColumnIndexOrThrow("personalContactId")),
                    positionName = cursor.getString(cursor.getColumnIndexOrThrow("positionName")),
                    departmentName = cursor.getString(cursor.getColumnIndexOrThrow("departmentName")),
                    personalContactName = cursor.getString(cursor.getColumnIndexOrThrow("personalContactName")),
                    personalContactNickName = cursor.getString(cursor.getColumnIndexOrThrow("personalContactNickName")),
                    personalContactEmail = cursor.getString(cursor.getColumnIndexOrThrow("personalContactEmail")),
                    personalContactMP = cursor.getString(cursor.getColumnIndexOrThrow("personalContactMP")),
                    personalContactMemo = cursor.getString(cursor.getColumnIndexOrThrow("personalContactMemo")),
                    personalContactBirthday = LocalDate.parse(
                        cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                "personalContactBirthday"
                            )
                        )
                    ),
                    company = getCompany(cursor.getInt(cursor.getColumnIndexOrThrow("companyId"))),
                    empId = cursor.getInt(cursor.getColumnIndexOrThrow("empId"))
                )
                contacts.add(contact)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return contacts
    }

    // Helper functions to get related entities
    private fun getCompany(companyId: Int): CompanyDTO {
        val db = this.readableDatabase
        val cursor = db.query(
            COMPANY_TABLE, null, "companyId=?", arrayOf(companyId.toString()), null, null, null
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

    private fun getPosition(positionId: Int): PositionDTO {
        val db = this.readableDatabase
        val cursor = db.query(
            POSITION_TABLE, null, "positionId=?", arrayOf(positionId.toString()), null, null, null
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

    private fun getDepartment(departmentId: Int): DepartmentDTO {
        val db = this.readableDatabase
        val cursor = db.query(
            DEPARTMENT_TABLE,
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

    fun getApprovalWithEMPDrafter(empId : Int): List<Pair<ApprovalEntity, ApprovalEmpDTO>> {
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
                da.*, e.*, p.name AS $POSITION_TABLE, d.name AS $DEPARTMENT_TABLE
            FROM digital_approval da
            JOIN $EMP_TABLE e ON da.drafter_id = e.empId
            JOIN $POSITION_TABLE p ON e.positionId = p.position_id
            JOIN $DEPARTMENT_TABLE d ON e.departmentId = d.department_id
            WHERE da.drafter_id = ? 
            OR (da.drafter_id IN (SELECT empId FROM $EMP_TABLE WHERE departmentId = (SELECT departmentId FROM $EMP_TABLE WHERE empId = ?) AND positionId = 2))
            OR (? IN (SELECT empId FROM $EMP_TABLE WHERE positionId = 1))
        """.trimIndent()

        val cursor = db.rawQuery(selectQuery, arrayOf(empId.toString(), empId.toString(), empId.toString()))

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

    fun getPersonalContactsWithGroups(): List<Pair<PersonalContactDTO, List<PersonalGroupDTO>>> {
        val contactsWithGroups = mutableListOf<Pair<PersonalContactDTO, List<PersonalGroupDTO>>>()
        val db = this.readableDatabase

        // 개인 주소록 데이터와 개인 그룹 데이터를 조인하는 쿼리
        val selectQuery = """
        SELECT 
            pc.personalContactId, pc.positionName, pc.departmentName, pc.personalContactName, 
            pc.personalContactNickName, pc.personalContactEmail, pc.personalContactMP, 
            pc.personalContactMemo, pc.personalContactBirthday, pc.companyId, pc.empId, 
            c.companyName, c.companyAddress, c.companyURL, c.companyNumber, c.companyFax,
            pg.personalGroupId, pg.personalGroupName
        FROM $PERSONAL_CONTACT_TABLE pc
        LEFT JOIN $COMPANY_TABLE c ON pc.companyId = c.companyId
        LEFT JOIN $PERSONAL_GROUP_TABLE pg ON pc.empId = pg.empId
        ORDER BY pc.personalContactId
    """.trimIndent()

        val cursor = db.rawQuery(selectQuery, null)

        var currentContact: PersonalContactDTO? = null
        val currentGroups = mutableListOf<PersonalGroupDTO>()

        if (cursor.moveToFirst()) {
            do {
                val personalContactId =
                    cursor.getInt(cursor.getColumnIndexOrThrow("personalContactId"))
                if (currentContact == null || currentContact.personalContactId != personalContactId) {
                    if (currentContact != null) {
                        contactsWithGroups.add(Pair(currentContact, currentGroups.toList()))
                    }
                    currentGroups.clear()
                    currentContact = PersonalContactDTO(
                        personalContactId = personalContactId,
                        positionName = cursor.getString(cursor.getColumnIndexOrThrow("positionName")),
                        departmentName = cursor.getString(cursor.getColumnIndexOrThrow("departmentName")),
                        personalContactName = cursor.getString(cursor.getColumnIndexOrThrow("personalContactName")),
                        personalContactNickName = cursor.getString(cursor.getColumnIndexOrThrow("personalContactNickName")),
                        personalContactEmail = cursor.getString(cursor.getColumnIndexOrThrow("personalContactEmail")),
                        personalContactMP = cursor.getString(cursor.getColumnIndexOrThrow("personalContactMP")),
                        personalContactMemo = cursor.getString(cursor.getColumnIndexOrThrow("personalContactMemo")),
                        personalContactBirthday = LocalDate.parse(
                            cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                    "personalContactBirthday"
                                )
                            )
                        ),
                        company = CompanyDTO(
                            companyId = cursor.getInt(cursor.getColumnIndexOrThrow("companyId")),
                            companyName = cursor.getString(cursor.getColumnIndexOrThrow("companyName")),
                            companyAddress = cursor.getString(cursor.getColumnIndexOrThrow("companyAddress")),
                            companyURL = cursor.getString(cursor.getColumnIndexOrThrow("companyURL")),
                            companyNumber = cursor.getString(cursor.getColumnIndexOrThrow("companyNumber")),
                            companyFax = cursor.getString(cursor.getColumnIndexOrThrow("companyFax"))
                        ),
                        empId = cursor.getInt(cursor.getColumnIndexOrThrow("empId"))
                    )
                }

                val personalGroupId = cursor.getInt(cursor.getColumnIndexOrThrow("personalGroupId"))
                if (personalGroupId != 0) {
                    val personalGroup = PersonalGroupDTO(
                        personalGroupId = personalGroupId,
                        empId = cursor.getInt(cursor.getColumnIndexOrThrow("empId")),
                        personalGroupName = cursor.getString(cursor.getColumnIndexOrThrow("personalGroupName"))
                    )
                    currentGroups.add(personalGroup)
                }
            } while (cursor.moveToNext())

            if (currentContact != null) {
                contactsWithGroups.add(Pair(currentContact, currentGroups.toList()))
            }
        }
        cursor.close()
        db.close()
        return contactsWithGroups
    }

    fun getAllPersonalGroups(): List<PersonalGroupDTO> {
        val personalGroups = mutableListOf<PersonalGroupDTO>()
        val db = this.readableDatabase
        val cursor: Cursor = db.query(PERSONAL_GROUP_TABLE, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val personalGroup = PersonalGroupDTO(
                    personalGroupId = cursor.getInt(cursor.getColumnIndexOrThrow("personalGroupId")),
                    empId = cursor.getInt(cursor.getColumnIndexOrThrow("empId")),
                    personalGroupName = cursor.getString(cursor.getColumnIndexOrThrow("personalGroupName"))
                )
                personalGroups.add(personalGroup)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return personalGroups
    }
}
