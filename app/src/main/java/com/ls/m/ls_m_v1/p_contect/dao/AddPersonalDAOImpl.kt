package com.ls.m.ls_m_v1.p_contect.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.p_contect.entity.CompanyDTO

class AddPersonalDAOImpl(context: Context): AddPersonalDAO {
    private val dbHelper = DatabaseHelper(context)

    override fun getAllCompanyData(): List<CompanyDTO> {
        val db:SQLiteDatabase = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.COMPANY_TABLE,
            arrayOf("companyId","companyName" , "companyAddress","companyURL", "companyNumber", "companyFax" ),
            null, null, null, null, null
        )

        val companys = mutableListOf<CompanyDTO>()
        if (cursor.moveToFirst()){
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("companyId"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("companyName"))
                val address = cursor.getString(cursor.getColumnIndexOrThrow("companyAddress"))
                val url = cursor.getString(cursor.getColumnIndexOrThrow("companyURL"))
                val officeNumber = cursor.getString(cursor.getColumnIndexOrThrow("companyNumber"))
                val fax = cursor.getString(cursor.getColumnIndexOrThrow("companyFax"))
                companys.add(CompanyDTO(id,name, address, url, officeNumber,fax))
            }while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return companys
    }
}