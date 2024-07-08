package com.ls.m.ls_m_v1.p_contect.dto

import com.ls.m.ls_m_v1.p_contect.entity.CompanyDTO
import java.time.LocalDate

data class AddPersonalDTO(
    val positionName :String,
    val departmentName :String,
    val personalContactName :String,
    val personalContactNickName :String,
    val personalContactEmail :String,
    val personalContactMP :String,
    val personalContactMemo :String,
    val personalContactBirthday :LocalDate?,
    val company : CompanyDTO,
    val empId : Int
)
