package com.ls.m.ls_m_v1.p_contect

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.databinding.ActivityModifyPersonalBinding
import com.ls.m.ls_m_v1.p_contect.dao.AddPersonalDAOImpl
import com.ls.m.ls_m_v1.p_contect.dao.AddPersonalDTO
import com.ls.m.ls_m_v1.p_contect.entity.CompanyDTO
import com.ls.m.ls_m_v1.p_contect.service.P_ContectService
import com.ls.m.ls_m_v1.p_contect.service.contectRepository
import java.time.LocalDate
import java.time.format.DateTimeParseException

class ModifyPersonal : AppCompatActivity() {
    private lateinit var binding: ActivityModifyPersonalBinding
    private lateinit var personalService: P_ContectService
    private lateinit var context: Context
    private var companyId: Int? = null
    private var companyName: String? = null
    private var companyInfoLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyPersonalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        context = this
        personalService = contectRepository().api

        val personalDao = AddPersonalDAOImpl(this)
        val companys = personalDao.getAllCompanyData()

        val items = companys.map { it.companyName }.toMutableList()
        items.add(0, "직접 입력")

        // Spinner의 항목이 선택 되었을때 동작을 설정합니다.
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position) as String
                // 회사가 선택되면 회사 정보 레이아웃을 표시하고, 그렇지 않으면 숨김
                if (selectedItem != "직접 입력") {
                    binding.companyInfoStub.visibility = View.GONE
                    val selectedCompany = companys.find { it.companyName == selectedItem }
                    selectedCompany?.let {
                        companyId = it.companyId
                        companyName = it.companyName
                    }
                } else {
                    binding.companyInfoStub.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        val company = intent.getStringExtra("company")
        val department = intent.getStringExtra("department")
        val mobilePhone = intent.getStringExtra("mobilePhone")
        val officePhone = intent.getStringExtra("officePhone")
        val position = intent.getStringExtra("position")
        val birthday = intent.getStringExtra("birthday")

        // X누르면 화면 종료
        binding.backButton1.setOnClickListener {
            finish()
        }

        binding.submit.setOnClickListener {
            val birthdayStr = binding.personalContactBirthday.text.toString()
            val birthdayAdd: LocalDate? = if (birthdayStr.isNotEmpty()) {
                try {
                    LocalDate.parse(birthdayStr)
                } catch (e: DateTimeParseException) {
                    Toast.makeText(context, "날짜 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                    null
                }
            } else {
                null
            }
            val addData = AddPersonalDTO(
                personalContactName = binding.personalContactName.text.toString(),
                personalContactNickName = binding.personalContactNickName.text.toString(),
                departmentName = binding.departmentName.text.toString(),
                positionName = binding.positionName.text.toString(),
                personalContactMemo = binding.personalContactMemo.text.toString(),
                personalContactMP = binding.personalContactMP.text.toString(),
                personalContactEmail = binding.personalContactEmail.text.toString(),
                personalContactBirthday = birthdayAdd,
                company = CompanyDTO(
                    companyId = binding.spinner.id ?: 0,
                    companyName = companyInfoLayout?.findViewById<EditText>(R.id.companyName)?.text.toString(),
                    companyAddress = companyInfoLayout?.findViewById<EditText>(R.id.companyAddress)?.text.toString(),
                    companyNumber = companyInfoLayout?.findViewById<EditText>(R.id.companyNumber)?.text.toString(),
                    companyURL = companyInfoLayout?.findViewById<EditText>(R.id.companyURL)?.text.toString(),
                    companyFax = companyInfoLayout?.findViewById<EditText>(R.id.companyFax)?.text.toString()
                ), empId = 21
                // 로그인 할때 변경할 것
            )


        }
    }
}