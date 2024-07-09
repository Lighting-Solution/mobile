package com.ls.m.ls_m_v1.p_contect.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

data class ContanctAandroidDTO(
    val personalContactDTOList : List<PersonalContactDTO>,
    val personalGroupDTOList : List<PersonalGroupDTO>,
    val contactGroupDTOList : List<ContactGroupDTO>
)

data class PersonalContactDTO(
    val personalContactId : Int,
    val positionName :String,
    val departmentName :String,
    val personalContactName :String,
    val personalContactNickName :String,
    val personalContactEmail :String,
    val personalContactMP :String,
    val personalContactMemo :String,
    val personalContactBirthday :LocalDate,
    val company :CompanyDTO,
    val empId : Int
)
@Parcelize
data class CompanyDTO  (
    var companyId : Int,
    val companyName : String,
    val companyAddress : String,
    val companyURL : String,
    val companyNumber : String,
    val companyFax : String
) : Parcelable

data class PersonalGroupDTO (
    val personalGroupId : Int,
    val empId : Int,
    val personalGroupName : String
)
data class ContactGroupDTO (
    val contactGroupId : Int,
    val personalContactId : Int,
    val personalGroupId : Int
)