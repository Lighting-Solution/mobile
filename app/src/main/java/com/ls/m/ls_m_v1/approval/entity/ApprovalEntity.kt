package com.ls.m.ls_m_v1.approval.entity

import com.ls.m.ls_m_v1.emp.entity.EmpDTO

data class ApprovalEntity(
    val digitalApprovalId : Int,
    val drafterId : Int,
    val digitalApprovalName : String,
    val digitalApprovalPath : String,
    val digitalApprovalType : Boolean,
    val drafterStatus : Boolean,
    val managerStatus : Boolean,
    val ceoStatus : Boolean,
    val empId : Int
)
