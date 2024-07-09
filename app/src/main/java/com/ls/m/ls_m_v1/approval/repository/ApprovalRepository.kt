package com.ls.m.ls_m_v1.approval.repository

import android.content.Context
import com.ls.m.ls_m_v1.approval.entity.ApprovalEmpDTO
import com.ls.m.ls_m_v1.approval.entity.ApprovalEntity
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper

class ApprovalRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)


    // 기안자로 데이터 가져오기
    fun getApprovalWithEMPDrafter(empId: Int): List<Pair<ApprovalEntity, ApprovalEmpDTO>> {
        val ApprovalWithEMP = mutableListOf<Pair<ApprovalEntity, ApprovalEmpDTO>>()
        val db = dbHelper.readableDatabase
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
                da.*, e.*, p.positionName AS ${DatabaseHelper.DatabaseConstants.POSITION_TABLE}, d.departmentName AS ${DatabaseHelper.DatabaseConstants.DEPARTMENT_TABLE}
            FROM digital_approval da
            JOIN ${DatabaseHelper.DatabaseConstants.EMP_TABLE} e ON da.drafterId = e.empId
            JOIN ${DatabaseHelper.DatabaseConstants.POSITION_TABLE} p ON e.positionId = p.positionId
            JOIN ${DatabaseHelper.DatabaseConstants.DEPARTMENT_TABLE} d ON e.departmentId = d.departmentId
            WHERE da.drafterId = ? 
            OR (da.drafterId IN (SELECT empId FROM ${DatabaseHelper.DatabaseConstants.EMP_TABLE} WHERE departmentId = (SELECT departmentId FROM ${DatabaseHelper.DatabaseConstants.EMP_TABLE} WHERE empId = ?) AND positionId = 2))
            OR (? IN (SELECT empId FROM ${DatabaseHelper.DatabaseConstants.EMP_TABLE} WHERE positionId = 1))
        """.trimIndent()

        val cursor =
            db.rawQuery(selectQuery, arrayOf(empId.toString(), empId.toString(), empId.toString()))

        with(cursor) {
            while (moveToNext()) {
                val pdocumentId = getInt(getColumnIndexOrThrow("digitalApprovalId"))
                val drafterId = getInt(getColumnIndexOrThrow("drafterId"))
                val documentName = getString(getColumnIndexOrThrow("digitalApprovalName"))
                val pdocumentPath = getString(getColumnIndexOrThrow("digitalApprovalPath"))
                val dBoxType = getInt(getColumnIndexOrThrow("digitalApprovalType"))
                val drafterStatus = getInt(getColumnIndexOrThrow("drafterStatus"))
                val managerStatus = getInt(getColumnIndexOrThrow("managerStatus"))
                val ceoStatus = getInt(getColumnIndexOrThrow("ceoStatus"))
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
                    digitalApprovalCreateAt = java.time.LocalDateTime.parse(createdAt).toString(),
                    digitalApprovalAt = java.time.LocalDateTime.parse(approvalAt).toString(),
                    managerRejectAt = java.time.LocalDateTime.parse(managerRejectAt).toString(),
                    ceoRejectAt = java.time.LocalDateTime.parse(ceoRejectAt).toString()
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

    fun getApprovalsForUser(empId: Int): List<ApprovalEntity> {
        val approvals = mutableListOf<ApprovalEntity>()
        val db = dbHelper.readableDatabase

        val selectQuery = """
            SELECT 
                da.digitalApprovalId, da.empId, da.drafterId, da.digitalApprovalName, da.digitalApprovalPath,
                da.digitalApprovalType, da.drafterStatus, da.managerStatus, da.ceoStatus,
                da.digitalApprovalCreateAt, da.digitalApprovalAt, da.managerRejectAt, da.ceoRejectAt
            FROM digital_approval da
            JOIN emp e ON da.drafterId = e.empId
            JOIN position p ON e.positionId = p.positionId
            JOIN department d ON e.departmentId = d.departmentId
            WHERE da.drafterId = ? 
            OR (da.drafterId IN (SELECT empId FROM emp WHERE departmentId = (SELECT departmentId FROM emp WHERE empId = ?) AND positionId = 2))
            OR (? IN (SELECT empId FROM emp WHERE positionId = 1))
        """.trimIndent()

        val cursor = db.rawQuery(selectQuery, arrayOf(empId.toString(), empId.toString(), empId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val approval = ApprovalEntity(
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
                    digitalApprovalAt = cursor.getString(cursor.getColumnIndexOrThrow("digitalApprovalAt")),
                    managerRejectAt = cursor.getString(cursor.getColumnIndexOrThrow("managerRejectAt")),
                    ceoRejectAt = cursor.getString(cursor.getColumnIndexOrThrow("ceoRejectAt"))
                )
                approvals.add(approval)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return approvals
    }
}