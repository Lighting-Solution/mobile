package com.ls.m.ls_m_v1.approval.entity

import com.ls.m.ls_m_v1.emp.entity.EmpDTO
import java.time.LocalDateTime

data class ApprovalEntity(
    val digitalApprovalId : Int,            // 개인문서 함
    val empId : Int,                        // 사원
    val drafterId : Int,                    // 기안자
    val digitalApprovalName : String,       // 문서명
    val digitalApprovalPath : String,       // 문서 경로
    val digitalApprovalType : Boolean,      // 문서함 종류
    val drafterStatus : Boolean,            // 기안자 결재 상태
    val managerStatus : Boolean,            // 부장 결재 상태
    val ceoStatus : Boolean,                // 대표이사 결재상태
    val digitalApprovalCreateAt : LocalDateTime,// 작성일
    val digitalApprovalAt : LocalDateTime,  // 결재 완료 일
    val managerRejectAt :LocalDateTime,     // 부장 반려일
    val ceoRejectAt:LocalDateTime           // 대표이사 반려일
)

data class ApprovalEmpDTO(
    val empId : Int,
    val empName : String,
    val position: String,
    val department : String
)