package com.ls.m.ls_m_v1.p_contect.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.p_contect.entity.CompanyDTO
import com.ls.m.ls_m_v1.p_contect.entity.ContactGroupDTO
import com.ls.m.ls_m_v1.p_contect.entity.PersonalContactDTO
import com.ls.m.ls_m_v1.p_contect.entity.PersonalGroupDTO
import java.time.LocalDate

class PersonalContactRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Insert Personal Contact
    fun insertPersonalContact(contact: PersonalContactDTO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("personalContactId", contact.personalContactId)
            put("positionName", contact.positionName)
            put("departmentName", contact.departmentName)
            put("personalContactName", contact.personalContactName)
            put("personalContactNickName", contact.personalContactNickName)
            put("personalContactEmail", contact.personalContactEmail)
            put("personalContactMP", contact.personalContactMP)
            put("personalContactMemo", contact.personalContactMemo)
            put("personalContactBirthday", contact.personalContactBirthday.toString())
            put("companyId", contact.company.companyId)
            put("empId", contact.empId)
        }
        db.insert(DatabaseHelper.DatabaseConstants.PERSONAL_CONTACT_TABLE, null, values)
    }

    // Get All Personal Contacts
    fun getAllPersonalContacts(): List<PersonalContactDTO> {
        val contacts = mutableListOf<PersonalContactDTO>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor =
            db.query(DatabaseHelper.DatabaseConstants.PERSONAL_CONTACT_TABLE, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val contact = PersonalContactDTO(
                    personalContactId = cursor.getInt(cursor.getColumnIndexOrThrow("personalContactId")),
                    positionName = cursor.getString(cursor.getColumnIndexOrThrow("positionName")),
                    departmentName = cursor.getString(cursor.getColumnIndexOrThrow("departmentName")),
                    personalContactName = cursor.getString(cursor.getColumnIndexOrThrow("personalContactName")),
                    personalContactNickName = cursor.getString(cursor.getColumnIndexOrThrow("personalContactNickName")),
                    personalContactEmail = cursor.getString(cursor.getColumnIndexOrThrow("personalContactEmail")),
                    personalContactMP = cursor.getString(cursor.getColumnIndexOrThrow("personalContactMP")),
                    personalContactMemo = cursor.getString(cursor.getColumnIndexOrThrow("personalContactMemo")),
                    personalContactBirthday = LocalDate.parse(
                        cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                "personalContactBirthday"
                            )
                        )
                    ),
                    company = dbHelper.getCompany(cursor.getInt(cursor.getColumnIndexOrThrow("companyId"))),
                    empId = cursor.getInt(cursor.getColumnIndexOrThrow("empId"))
                )
                contacts.add(contact)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return contacts
    }


    fun getPersonalContactsWithGroups(): List<Pair<PersonalContactDTO, List<PersonalGroupDTO>>> {
        val contactsWithGroups = mutableListOf<Pair<PersonalContactDTO, List<PersonalGroupDTO>>>()
        val db = dbHelper.readableDatabase

        // 개인 주소록 데이터와 개인 그룹 데이터를 조인하는 쿼리
        val selectQuery = """
        SELECT 
            pc.personalContactId, pc.positionName, pc.departmentName, pc.personalContactName, 
            pc.personalContactNickName, pc.personalContactEmail, pc.personalContactMP, 
            pc.personalContactMemo, pc.personalContactBirthday, pc.companyId, pc.empId, 
            c.companyName, c.companyAddress, c.companyURL, c.companyNumber, c.companyFax,
            pg.personalGroupId, pg.personalGroupName
        FROM ${DatabaseHelper.DatabaseConstants}.PERSONAL_CONTACT_TABLE pc
        LEFT JOIN ${DatabaseHelper.DatabaseConstants}.COMPANY_TABLE c ON pc.companyId = c.companyId
        LEFT JOIN ${DatabaseHelper.DatabaseConstants}.PERSONAL_GROUP_TABLE pg ON pc.empId = pg.empId
        ORDER BY pc.personalContactId
    """.trimIndent()

        val cursor = db.rawQuery(selectQuery, null)

        var currentContact: PersonalContactDTO? = null
        val currentGroups = mutableListOf<PersonalGroupDTO>()

        if (cursor.moveToFirst()) {
            do {
                val personalContactId =
                    cursor.getInt(cursor.getColumnIndexOrThrow("personalContactId"))
                if (currentContact == null || currentContact.personalContactId != personalContactId) {
                    if (currentContact != null) {
                        contactsWithGroups.add(Pair(currentContact, currentGroups.toList()))
                    }
                    currentGroups.clear()
                    currentContact = PersonalContactDTO(
                        personalContactId = personalContactId,
                        positionName = cursor.getString(cursor.getColumnIndexOrThrow("positionName")),
                        departmentName = cursor.getString(cursor.getColumnIndexOrThrow("departmentName")),
                        personalContactName = cursor.getString(cursor.getColumnIndexOrThrow("personalContactName")),
                        personalContactNickName = cursor.getString(cursor.getColumnIndexOrThrow("personalContactNickName")),
                        personalContactEmail = cursor.getString(cursor.getColumnIndexOrThrow("personalContactEmail")),
                        personalContactMP = cursor.getString(cursor.getColumnIndexOrThrow("personalContactMP")),
                        personalContactMemo = cursor.getString(cursor.getColumnIndexOrThrow("personalContactMemo")),
                        personalContactBirthday = LocalDate.parse(
                            cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                    "personalContactBirthday"
                                )
                            )
                        ),
                        company = CompanyDTO(
                            companyId = cursor.getInt(cursor.getColumnIndexOrThrow("companyId")),
                            companyName = cursor.getString(cursor.getColumnIndexOrThrow("companyName")),
                            companyAddress = cursor.getString(cursor.getColumnIndexOrThrow("companyAddress")),
                            companyURL = cursor.getString(cursor.getColumnIndexOrThrow("companyURL")),
                            companyNumber = cursor.getString(cursor.getColumnIndexOrThrow("companyNumber")),
                            companyFax = cursor.getString(cursor.getColumnIndexOrThrow("companyFax"))
                        ),
                        empId = cursor.getInt(cursor.getColumnIndexOrThrow("empId"))
                    )
                }

                val personalGroupId = cursor.getInt(cursor.getColumnIndexOrThrow("personalGroupId"))
                if (personalGroupId != 0) {
                    val personalGroup = PersonalGroupDTO(
                        personalGroupId = personalGroupId,
                        empId = cursor.getInt(cursor.getColumnIndexOrThrow("empId")),
                        personalGroupName = cursor.getString(cursor.getColumnIndexOrThrow("personalGroupName"))
                    )
                    currentGroups.add(personalGroup)
                }
            } while (cursor.moveToNext())

            if (currentContact != null) {
                contactsWithGroups.add(Pair(currentContact, currentGroups.toList()))
            }
        }
        cursor.close()
        db.close()
        return contactsWithGroups
    }

    fun getAllPersonalGroups(): List<PersonalGroupDTO> {
        val personalGroups = mutableListOf<PersonalGroupDTO>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor =
            db.query(DatabaseHelper.DatabaseConstants.PERSONAL_GROUP_TABLE, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val personalGroup = PersonalGroupDTO(
                    personalGroupId = cursor.getInt(cursor.getColumnIndexOrThrow("personalGroupId")),
                    empId = cursor.getInt(cursor.getColumnIndexOrThrow("empId")),
                    personalGroupName = cursor.getString(cursor.getColumnIndexOrThrow("personalGroupName"))
                )
                personalGroups.add(personalGroup)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return personalGroups
    }
//    fun insertCompany(company: CompanyDTO): Long {
//        // 1번 제외 후 저장 할것
//        val db = dbHelper.writableDatabase
//        val values = ContentValues().apply {
//            put("companyName", company.companyName)
//            put("companyAddress", company.companyAddress)
//            put("companyURL", company.companyURL)
//            put("companyNumber", company.companyNumber)
//            put("companyFax", company.companyFax)
//        }
//        val id = db.insert(DatabaseHelper.DatabaseConstants.COMPANY_TABLE, null, values)
//        db.close()
//        return id
//    }

   fun getAllCompanyData(): List<CompanyDTO> {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.DatabaseConstants.COMPANY_TABLE,
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

    fun insertCompany(company: CompanyDTO) {
        if (company.companyId != 1) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("companyId", company.companyId)
                put("companyName", company.companyName)
                put("companyAddress", company.companyAddress)
                put("companyURL", company.companyURL)
                put("companyNumber", company.companyNumber)
                put("companyFax", company.companyFax)
            }
            db.insert("company", null, values)
        }
    }

    fun insertPersonalGroup(group: PersonalGroupDTO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("personalGroupId", group.personalGroupId)
            put("empId", group.empId)
            put("personalGroupName", group.personalGroupName)
        }
        db.insert("personal_group", null, values)
    }

    fun insertContactGroup(contactGroup: ContactGroupDTO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("contactGroupId", contactGroup.contactGroupId)
            put("personalContactId", contactGroup.personalContactId)
            put("personalGroupId", contactGroup.personalGroupId)
        }
        db.insert("contact_group", null, values)
    }

    fun forRefresh(){
        val db = dbHelper.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseHelper.DatabaseConstants.PERSONAL_CONTACT_TABLE}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseHelper.DatabaseConstants.PERSONAL_GROUP_TABLE}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseHelper.DatabaseConstants.CONTACT_GROUP_TABLE}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseHelper.DatabaseConstants.COMPANY_TABLE}")
        dbHelper.onCreate(db)
    }
}