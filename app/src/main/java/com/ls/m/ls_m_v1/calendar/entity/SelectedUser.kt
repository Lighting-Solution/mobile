package com.ls.m.ls_m_v1.calendar.entity

import android.os.Parcelable
import com.ls.m.ls_m_v1.p_contect.entity.CompanyDTO
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class SelectedUser(
    val id: String,
    val name: String,
    val position: String,
    val department:String,
    val mobilePhone: String,
    val officePhone: String,
    val company: @RawValue CompanyDTO,
    val memo: String = "",
    var isSelected : Boolean
):Parcelable
