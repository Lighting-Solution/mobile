package com.ls.m.ls_m_v1.emp.entity

import com.ls.m.ls_m_v1.p_contect.entity.CompanyDTO

data class AllContact(
    val id: String,
    val name: String,
    val position: String,
    val department:String,
    val email: String,
    val mobilePhone: String,
    val officePhone: String,
    val birthday: String,
    val company: CompanyDTO,
    val buttonState : Boolean = false
)

data class SectionHeader(
    val title: String
)
