package com.ls.m.ls_m_v1.p_contect.dao

import com.ls.m.ls_m_v1.p_contect.entity.CompanyDTO

interface AddPersonalDAO {
    fun getAllCompanyData (): List<CompanyDTO>
}