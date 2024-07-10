package com.ls.m.ls_m_v1.approval.repository

import android.content.Context
import com.ls.m.ls_m_v1.approval.entity.ApprovalEmpDTO
import com.ls.m.ls_m_v1.approval.entity.ApprovalEntity
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper

class ApprovalRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun getApprovalsForUser(empId: Int): List<Pair<ApprovalEntity, ApprovalEmpDTO>> {
        val approvalWithEMP = mutableListOf<Pair<ApprovalEntity, ApprovalEmpDTO>>()
        val db = dbHelper.readableDatabase

        val selectQuery = """
            SELECT 
                da.*, e.*, p.positionName AS position, d.departmentName AS department
            FROM digital_approval da
            JOIN emp e ON da.drafterId = e.empId
            JOIN position p ON e.positionId = p.positionId
            JOIN department d ON e.departmentId = d.departmentId
            WHERE da.drafterId = ? 
            OR (EXISTS (
                SELECT 1 
                FROM emp e2 
                WHERE ABS(e2.departmentId / 100) % 10 = 
                      (SELECT ABS(e.departmentId / 100) % 10 
                       FROM emp e 
                       WHERE e.empId = da.drafterId)
                AND e2.positionId = 2
                AND e2.empId = ?
            ))
            OR (? IN (SELECT empId FROM emp WHERE positionId = 1))
        """.trimIndent()

        val cursor = db.rawQuery(selectQuery, arrayOf(empId.toString(), empId.toString(),empId.toString()))

        try {
            if (cursor.moveToFirst()) {
                do {
                    val approvalEntity = ApprovalEntity(
                        digitalApprovalId = cursor.getInt(cursor.getColumnIndexOrThrow("digitalApprovalId")),
                        empId = cursor.getInt(cursor.getColumnIndexOrThrow("empId")),
                        drafterId = cursor.getInt(cursor.getColumnIndexOrThrow("drafterId")),
                        digitalApprovalName = cursor.getString(cursor.getColumnIndexOrThrow("digitalApprovalName")),
                        digitalApprovalPath = cursor.getString(cursor.getColumnIndexOrThrow("digitalApprovalPath")),
                        digitalApprovalType = cursor.getInt(cursor.getColumnIndexOrThrow("digitalApprovalType")),
                        drafterStatus = cursor.getInt(cursor.getColumnIndexOrThrow("drafterStatus")),
                        managerStatus = cursor.getInt(cursor.getColumnIndexOrThrow("managerStatus")),
                        ceoStatus = cursor.getInt(cursor.getColumnIndexOrThrow("ceoStatus")),
                        digitalApprovalCreateAt = cursor.getString(cursor.getColumnIndexOrThrow("digitalApprovalCreateAt")),
                        digitalApprovalAt = cursor.getString(cursor.getColumnIndexOrThrow("digitalApprovalAt"))?: "",
                        managerRejectAt = cursor.getString(cursor.getColumnIndexOrThrow("managerRejectAt"))?: "",
                        ceoRejectAt = cursor.getString(cursor.getColumnIndexOrThrow("ceoRejectAt"))?: ""
                    )

                    val approvalEmpDTO = ApprovalEmpDTO(
                        empId = cursor.getInt(cursor.getColumnIndexOrThrow("empId")),
                        empName = cursor.getString(cursor.getColumnIndexOrThrow("empName")),
                        position = cursor.getString(cursor.getColumnIndexOrThrow("position")),
                        department = cursor.getString(cursor.getColumnIndexOrThrow("department"))
                    )

                    approvalWithEMP.add(Pair(approvalEntity, approvalEmpDTO))
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
            db.close()
        }

        return approvalWithEMP
    }
}
