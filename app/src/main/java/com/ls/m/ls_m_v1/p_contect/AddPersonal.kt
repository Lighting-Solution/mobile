package com.ls.m.ls_m_v1.p_contect

import android.app.DatePickerDialog
import android.content.Context
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
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.databinding.ActivityAddPersonalBinding
import com.ls.m.ls_m_v1.p_contect.dto.AddPersonalDTO
import com.ls.m.ls_m_v1.p_contect.entity.CompanyDTO
import com.ls.m.ls_m_v1.p_contect.repository.PersonalContactRepository
import com.ls.m.ls_m_v1.p_contect.service.P_ContectService
import com.ls.m.ls_m_v1.p_contect.service.contectRepository
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.Calendar

class AddPersonal : AppCompatActivity() {
    private lateinit var binding: ActivityAddPersonalBinding
    private lateinit var context: Context
    private var companyId: Int? = null
    private var companyName: String? = null
    private var companyInfoLayout: LinearLayout? = null
    private lateinit var personalService: P_ContectService
    private val repository = PersonalContactRepository(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPersonalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        context = this
        personalService = contectRepository().api

        val companyList = repository.getAllCompanyData()

        val items = companyList.map { it.companyName }.toMutableList()
        items.add(0, "직접 입력")

        // 메모 활성화 컬러
        binding.personalContactMemo.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                binding.parsonalMemoLayout.setBoxStrokeColorStateList(ColorStateList.valueOf(Color.parseColor("#a97d6a")))
                binding.personalContactMemo.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#a97d6a")))
            }
        }

        //Arrayabapter 를 사용하여  spinner에 항목을 설정합니다.
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        binding.spinner.adapter = adapter

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
                    val selectedCompany = companyList.find { it.companyName == selectedItem }
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

        binding.backButton1.setOnClickListener {
            finish()
        }

        binding.personalContactBirthday.setOnClickListener {
            showDatePickerDialog { date -> binding.personalContactBirthday.text = date }
        }
        binding.submit.setOnClickListener {
            val birthdayStr = binding.personalContactBirthday.text.toString()
            val birthday: LocalDate? = if (birthdayStr.isNotEmpty()) {
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
                personalContactBirthday = birthday,
                company = CompanyDTO(
                    companyId = binding.spinner.id ?: 0,
                    companyName = companyInfoLayout?.findViewById<EditText>(R.id.companyName)?.text.toString(),
                    companyAddress = companyInfoLayout?.findViewById<EditText>(R.id.companyAddress)?.text.toString(),
                    companyNumber = companyInfoLayout?.findViewById<EditText>(R.id.companyNumber)?.text.toString(),
                    companyURL = companyInfoLayout?.findViewById<EditText>(R.id.companyURL)?.text.toString(),
                    companyFax = companyInfoLayout?.findViewById<EditText>(R.id.companyFax)?.text.toString()
                ),empId = 21
                // 로그인 할때 변경할 것
            )

            Log.d("ddddddd",binding.personalContactBirthday.text.toString() )

            // 등록 데이터 api요청으로 날림
//            if (binding.spinner.selectedItem == "직접 입력") {
//                // api 통신으로 보냄
//                personalService.addPersnalData(addData).enqueue(object : retrofit2.Callback<String> {
//                    override fun onResponse(call: Call<String>, response: Response<String>) {
//
//                        if (response.isSuccessful) {
//                            // 성공적으로 업데이트됨
//                            Toast.makeText(context, "회사 정보가 발송.", Toast.LENGTH_SHORT).show()
//                            // 성공하면 테이블 날리고 리프레쉬 할것.
//
//
//                        } else {
//                            // 업데이트 실패
//                            Toast.makeText(context, "회사 정보 발송에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    override fun onFailure(call: Call<String>, t: Throwable) {
//                        TODO("Not yet implemented")
//                    }
//
//                })

//            } else {
//                // 선택한 회사가 있으면 회사 정보를 읽어옴
//                addData.company.companyId = companyId ?: 0
//                personalService.addPersnalData(addData).enqueue(object : retrofit2.Callback<String> {
//                    override fun onResponse(call: Call<String>, response: Response<String>) {
//                        Log.d("api", call.toString())
//                        Log.d("api", response.toString())
//
//                        if (response.isSuccessful) {
//                            // 성공적으로 업데이트됨
//                            Toast.makeText(context, "회사 정보가 발송.", Toast.LENGTH_SHORT).show()
//                        } else {
//                            // 업데이트 실패
//                            Toast.makeText(context, "회사 정보 발송에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    override fun onFailure(call: Call<String>, t: Throwable) {
//                        TODO("Not yet implemented")
//                    }
//
//                })
//            }

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
