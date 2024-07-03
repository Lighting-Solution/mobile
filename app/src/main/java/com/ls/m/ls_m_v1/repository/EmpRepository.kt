package com.ls.m.ls_m_v1.repository

import android.content.ContentValues
import android.content.Context
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.emp.entity.EmpDTO

class EmpRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Insert Emp
    fun insertEmp(emp: EmpDTO) {
        val db = dbHelper.writableDatabase
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
        db.insert(DatabaseHelper.EMP_TABLE, null, values)
    }

}