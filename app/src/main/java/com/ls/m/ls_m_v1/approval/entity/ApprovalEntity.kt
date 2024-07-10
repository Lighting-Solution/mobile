package com.ls.m.ls_m_v1.approval.entity

import java.io.Serializable

data class ApprovalEntity(
    val digitalApprovalId: Int,            // 개인문서 함
    val empId: Int,                        // 사원
    val drafterId: Int,                    // 기안자
    val digitalApprovalName: String,       // 문서명
    val digitalApprovalPath: String,       // 문서 경로
    val digitalApprovalType: Int,      // 문서함 종류
    val drafterStatus: Int,            // 기안자 결재 상태
    var managerStatus: Int,            // 부장 결재 상태
    var ceoStatus: Int,                // 대표이사 결재상태
    val digitalApprovalCreateAt: String,// 작성일
    val digitalApprovalAt: String,  // 결재 완료 일
    val managerRejectAt: String,     // 부장 반려일
    val ceoRejectAt: String           // 대표이사 반려일
):Serializable

data class ApprovalEmpDTO(
    val empId : Int,
    val empName : String,
    val position: String,
    val department : String
)