package com.ls.m.ls_m_v1.p_contect

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ls.m.ls_m_v1.MainActivity
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.databinding.ActivityAddPersonalBinding
import com.ls.m.ls_m_v1.login.repository.LoginRepository
import com.ls.m.ls_m_v1.p_contect.dto.AddPersonalDTO
import com.ls.m.ls_m_v1.p_contect.entity.CompanyDTO
import com.ls.m.ls_m_v1.p_contect.entity.PersonalContactDTO
import com.ls.m.ls_m_v1.p_contect.repository.PersonalContactRepository
import com.ls.m.ls_m_v1.p_contect.service.P_ContectService
import com.ls.m.ls_m_v1.p_contect.service.RetrofitInstancePersonal
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.Calendar

class AddPersonal : AppCompatActivity() {
    private lateinit var binding: ActivityAddPersonalBinding
    private lateinit var context: Context
    private lateinit var personalService: P_ContectService
    private lateinit var loginRepository: LoginRepository
    private lateinit var personalContactRepository: PersonalContactRepository
    private lateinit var companyList: List<CompanyDTO>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPersonalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        context = this
        personalService = RetrofitInstancePersonal.api
        loginRepository = LoginRepository(this)
        personalContactRepository = PersonalContactRepository(this)

        companyList = personalContactRepository.getAllCompanyData()

        val items = companyList.map { it.companyName }.toMutableList()
        items.add(0, "직접 입력")

        // 메모 활성화 컬러
        binding.personalContactMemo.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.parsonalMemoLayout.setBoxStrokeColorStateList(
                    ColorStateList.valueOf(Color.parseColor("#a97d6a"))
                )
                binding.personalContactMemo.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#a97d6a")))
            }
        }

        // ArrayAdapter를 사용하여 spinner에 항목을 설정합니다.
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        binding.spinner.adapter = adapter

        // Spinner의 항목이 선택되었을 때 동작을 설정합니다.
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val selectedItem = items[position]
                if (selectedItem != "직접 입력") {
                    binding.companyInfoStub.visibility = View.GONE
                } else {
                    binding.companyInfoStub.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.backButton1.setOnClickListener {
            finish()
        }

        binding.personalContactBirthday.setOnClickListener {
            showDatePickerDialog { date -> binding.personalContactBirthday.text = date }
        }

        val loginData = loginRepository.getloginData()

        binding.submit.setOnClickListener {

            val selectedCompany = companyList.find { it.companyName == binding.spinner.selectedItem.toString() }
            val companyId = selectedCompany?.companyId ?: 0

            val addData = PersonalContactDTO(
                personalContactId = 0,
                personalContactName = binding.personalContactName.text.toString(),
                departmentName = binding.departmentName.text.toString(),
                positionName = binding.positionName.text.toString(),
                personalContactMemo = binding.personalContactMemo.text.toString(),
                personalContactMP = binding.personalContactMP.text.toString(),
                personalContactEmail = binding.personalContactEmail.text.toString(),
                personalContactBirthday = binding.personalContactBirthday.text.toString()?: "-",
                personalContactNickName = binding.personalContactNickName.text.toString(),
                company = if (binding.spinner.selectedItem == "직접 입력") {
                    CompanyDTO(
                        companyId = 0,
                        companyName = binding.companyInfoStub.findViewById<EditText>(R.id.companyName)?.text.toString(),
                        companyAddress = binding.companyInfoStub.findViewById<EditText>(R.id.companyAddress)?.text.toString(),
                        companyNumber = binding.companyInfoStub.findViewById<EditText>(R.id.companyNumber)?.text.toString(),
                        companyURL = binding.companyInfoStub.findViewById<EditText>(R.id.companyURL)?.text.toString(),
                        companyFax = binding.companyInfoStub.findViewById<EditText>(R.id.companyFax)?.text.toString()
                    )
                } else {
                    selectedCompany ?: CompanyDTO(companyId, "", "", "", "", "")
                },
                empId = loginData.empId
            )

            if (binding.spinner.selectedItem == "직접 입력") {
//                personalService.addPersnalData(loginData.token, addData).enqueue(object : retrofit2.Callback<String> {
//                    override fun onResponse(call: Call<String>, response: Response<String>) {
//                        if (response.isSuccessful) {
//                            Toast.makeText(context, "회사 정보가 발송되었습니다.", Toast.LENGTH_SHORT).show()
//                        } else {
//                            Toast.makeText(context, "회사 정보 발송에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<String>, t: Throwable) {
//                        Toast.makeText(context, "회사 정보 발송에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                })
                val companyId = personalContactRepository.insertCompany(addData.company)
                addData.company.companyId = companyId.toInt()

                personalContactRepository.createPersonalContact(addData)
            } else {
//                addData.company.companyId = binding.spinner.id
//                Log.d("dddd",binding.spinner.id.toString())
//                personalService.addPersnalData(loginData.token, addData).enqueue(object : retrofit2.Callback<String> {
//                    override fun onResponse(call: Call<String>, response: Response<String>) {
//                        if (response.isSuccessful) {
//                            Toast.makeText(context, "회사 정보가 발송되었습니다.", Toast.LENGTH_SHORT).show()
//                        } else {
//                            Toast.makeText(context, "회사 정보 발송에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<String>, t: Throwable) {
//                        Toast.makeText(context, "회사 정보 발송에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                })
                addData.company.companyId = binding.spinner.id
                personalContactRepository.createPersonalContact(addData)
            }
            val intent = Intent(this@AddPersonal, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("SELECTED_TAB", R.id.tab2)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun showDatePickerDialog(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                onDateSelected(date)
            },
            year, month, day
        )

        datePickerDialog.show()
    }
}
