package com.ls.m.ls_m_v1.calendar.entity

import android.os.Parcelable
import com.ls.m.ls_m_v1.p_contect.entity.CompanyDTO
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


data class SelectedUser(
    val id: String,//
    val name: String,//
    val position: String,
    val department: String,//
    val mobilePhone: String,
    val company: CompanyDTO
)
// dto 생성
data class participantDTO(
    val id: String,
    val name: String,
    val department: String
)

@Parcelize
data class CalendarEmp(
    val id: String,
    val name: String,
    val position: String,
    val department: String,
    val mobilePhone: String,
    val company: @RawValue CompanyDTO,
    var isSelected: Boolean = false
) : Parcelable
