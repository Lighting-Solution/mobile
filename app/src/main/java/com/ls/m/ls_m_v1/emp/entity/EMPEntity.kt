package com.ls.m.ls_m_v1.emp.entity

import com.ls.m.ls_m_v1.p_contect.entity.CompanyDTO
import java.time.LocalDate

data class EmpAndroidDTO(
    val empDTOList: List<EmpDTO>,
    val positionDTOList: List<PositionDTO>,
    val departmentDTOList: List<DepartmentDTO>
)

data class EmpDTO(
    val empId : Int,
    val empName : String,
    val empEmail : String,
    val empMP : String,
    val empMemo : String,
    val empHP: String,
    val empHomeAddress :String,
    val empHomeFax: String,
    val empBirthday: LocalDate,
    val company : CompanyDTO,
    val position: PositionDTO,
    val department : DepartmentDTO
)

data class PositionDTO(
    val positionId : Int,
    val positionName : String
)

data class DepartmentDTO(
    val departmentId : Int,
    val departmentName : String
)