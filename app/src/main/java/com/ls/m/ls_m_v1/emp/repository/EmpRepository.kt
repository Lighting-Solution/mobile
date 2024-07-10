package com.ls.m.ls_m_v1.emp.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.ls.m.ls_m_v1.approval.entity.ApprovalEmpDTO
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.emp.entity.DepartmentDTO
import com.ls.m.ls_m_v1.emp.entity.EmpDTO
import com.ls.m.ls_m_v1.emp.entity.PositionDTO
import java.time.LocalDate

class EmpRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Insert Emp
    fun insertEmp(empList: List<EmpDTO>) {
        val db = dbHelper.writableDatabase
        empList.forEach { emp ->
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
            db.insert(DatabaseHelper.DatabaseConstants.EMP_TABLE, null, values)
        }
    }

    fun insertPosition(positionList: List<PositionDTO>) {
        val db = dbHelper.writableDatabase
        positionList.forEach { position ->
            val value = ContentValues().apply {
                put("positionId", position.positionId)
                put("positionName", position.positionName)
            }
            db.insert(DatabaseHelper.DatabaseConstants.POSITION_TABLE, null, value)
        }
    }

    fun insertDepartment(departmentList: List<DepartmentDTO>) {
        val db = dbHelper.writableDatabase
        departmentList.forEach { department ->
            val value = ContentValues().apply {
                put("departmentId", department.departmentId)
                put("departmentName", department.departmentName)
            }
            db.insert(DatabaseHelper.DatabaseConstants.DEPARTMENT_TABLE, null, value)
        }
    }

    // Get All Emps
    fun getAllEmps(): List<EmpDTO> {
        val emps = mutableListOf<EmpDTO>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor =
            db.query(DatabaseHelper.DatabaseConstants.EMP_TABLE, null, null, null, null, null, null)
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
                    company = dbHelper.getCompany(cursor.getInt(cursor.getColumnIndexOrThrow("companyId"))),
                    position = dbHelper.getPosition(cursor.getInt(cursor.getColumnIndexOrThrow("positionId"))),
                    department = dbHelper.getDepartment(cursor.getInt(cursor.getColumnIndexOrThrow("departmentId")))
                )
                emps.add(emp)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return emps
    }

    fun getOneEmps(empId : Int): EmpDTO? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.DatabaseConstants.EMP_TABLE} WHERE empId = ?", arrayOf(empId.toString()))

        var value : EmpDTO? = null
        if (cursor.moveToFirst()){
            value = EmpDTO(
                empId = cursor.getInt(cursor.getColumnIndexOrThrow("empId")),
                empName = cursor.getString(cursor.getColumnIndexOrThrow("empName")),
                empEmail = cursor.getString(cursor.getColumnIndexOrThrow("empEmail")),
                empMP = cursor.getString(cursor.getColumnIndexOrThrow("empMP")),
                empMemo = cursor.getString(cursor.getColumnIndexOrThrow("empMemo")),
                empHP = cursor.getString(cursor.getColumnIndexOrThrow("empHP")),
                empHomeAddress = cursor.getString(cursor.getColumnIndexOrThrow("empHomeAddress")),
                empHomeFax = cursor.getString(cursor.getColumnIndexOrThrow("empHomeFax")),
                empBirthday = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow("empBirthday"))),
                company = dbHelper.getCompany(cursor.getInt(cursor.getColumnIndexOrThrow("companyId"))),
                position = dbHelper.getPosition(cursor.getInt(cursor.getColumnIndexOrThrow("positionId"))),
                department = dbHelper.getDepartment(cursor.getInt(cursor.getColumnIndexOrThrow("departmentId")))
            )
            cursor.close()
        }
        return value
    }

    fun getDrafter(empId: Int): ApprovalEmpDTO?{
        val db = dbHelper.readableDatabase
        val query = """
            SELECT empId, empName, positionId, departmentId FROM ${DatabaseHelper.DatabaseConstants.EMP_TABLE} WHERE empId = ?
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(empId.toString()))
        return if (cursor.moveToFirst()){
            ApprovalEmpDTO(
                empId = cursor.getInt(cursor.getColumnIndexOrThrow("empId")),
                empName = cursor.getString(cursor.getColumnIndexOrThrow("empName")),
                department = dbHelper.getDepartment(cursor.getInt(cursor.getColumnIndexOrThrow("departmentId"))).departmentName,
                position = dbHelper.getPosition(cursor.getInt(cursor.getColumnIndexOrThrow("positionId"))).positionName
            )
        }else{
            null
        }.also {
            cursor.close()
            db.close()
        }
    }
    fun getManager(empId : Int): ApprovalEmpDTO? {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT e2.*
            FROM ${DatabaseHelper.DatabaseConstants.EMP_TABLE} e2
            WHERE ABS(e2.departmentId / 100) % 10 = (
                SELECT ABS(e.departmentId / 100) % 10 
                FROM ${DatabaseHelper.DatabaseConstants.EMP_TABLE} e 
                WHERE e.empId = ?
            )
            AND e2.positionId = 2;
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(empId.toString()))
        return if (cursor.moveToFirst()){
            ApprovalEmpDTO(
                empId = cursor.getInt(cursor.getColumnIndexOrThrow("empId")),
                empName = cursor.getString(cursor.getColumnIndexOrThrow("empName")),
                department = dbHelper.getDepartment(cursor.getInt(cursor.getColumnIndexOrThrow("departmentId"))).departmentName,
                position = dbHelper.getPosition(cursor.getInt(cursor.getColumnIndexOrThrow("positionId"))).positionName
            )
        }else{
            null
        }.also {
            cursor.close()
            db.close()
        }
    }

    fun getCeo(): ApprovalEmpDTO?{
        val db = dbHelper.readableDatabase
        val query = """
            SELECT * FROM ${DatabaseHelper.DatabaseConstants.EMP_TABLE} WHERE  positionId = 1
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        return if (cursor.moveToFirst()){

            ApprovalEmpDTO(
                empId = cursor.getInt(cursor.getColumnIndexOrThrow("empId")),
                empName = cursor.getString(cursor.getColumnIndexOrThrow("empName")),
                department = dbHelper.getDepartment(cursor.getInt(cursor.getColumnIndexOrThrow("departmentId"))).departmentName,
                position = dbHelper.getPosition(cursor.getInt(cursor.getColumnIndexOrThrow("positionId"))).positionName
            )
        }else{
            null
        }.also {
            cursor.close()
            db.close()
        }
    }

    fun forRefreash() {
        val db = dbHelper.writableDatabase
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseHelper.DatabaseConstants.EMP_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseHelper.DatabaseConstants.DEPARTMENT_TABLE}")
        db?.execSQL("DROP TABLE IF EXISTS ${DatabaseHelper.DatabaseConstants.POSITION_TABLE}")
        dbHelper.onCreate(db)
    }

}