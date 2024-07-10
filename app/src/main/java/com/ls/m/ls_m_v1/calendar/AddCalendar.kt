package com.ls.m.ls_m_v1.calendar

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ls.m.ls_m_v1.approval.entity.ApprovalEntity
import com.ls.m.ls_m_v1.calendar.entity.CalendarDto
import com.ls.m.ls_m_v1.calendar.entity.CalendarEmp
import com.ls.m.ls_m_v1.calendar.entity.CalendarEntity
import com.ls.m.ls_m_v1.calendar.entity.SelectedUser
import com.ls.m.ls_m_v1.calendar.entity.participantDTO
import com.ls.m.ls_m_v1.databinding.ActivityAddCalendarBinding
import com.ls.m.ls_m_v1.emp.entity.EmpDTO
import com.ls.m.ls_m_v1.emp.repository.EmpRepository
import com.ls.m.ls_m_v1.login.entity.LoginResponseDto
import com.ls.m.ls_m_v1.login.repository.LoginRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class AddCalendar : AppCompatActivity() {
    private lateinit var binding: ActivityAddCalendarBinding
    private val timeList = mutableListOf<String>()
    private val selectedUsers = ArrayList<CalendarEmp>()
    private lateinit var loginRepository: LoginRepository
    private lateinit var empRepository: EmpRepository
    private var value: EmpDTO? = null
    private var empId: Int = 0
    private var calendarEmp: CalendarEmp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        empRepository = EmpRepository(this)

        val loginData = intent.getSerializableExtra("loginData")as? LoginResponseDto
        loginData?.let {
            value = empRepository.getOneEmps(loginData.empId)
            if (value != null) {
                calendarEmp = CalendarEmp(
                    id = value!!.empId.toString(),
                    name = value!!.empName,
                    position = value!!.position.positionName,
                    company = value!!.company,
                    department = value!!.department.departmentName,
                    mobilePhone = value!!.empMP,
                    isSelected = true
                )
            addChip(calendarEmp!!)
            }

        }

        // 기본적으로 오늘 날짜를 설정
        val todayDate = getCurrentDate()
        binding.startDate.text = todayDate
        binding.endDate.text = todayDate
        empRepository = EmpRepository(this)
        setupTimeList()
        setupTimeSpinners()
        setupListeners()
        initializeStartTime()

    }

//    private suspend fun getLoginData(): Int {
//        loginRepository = LoginRepository(this)
//        val value = loginRepository.getloginData()
//        return value.empId
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_ATTENDEES && resultCode == Activity.RESULT_OK) {
            Log.d("AddCalendar", "onActivityResult called")
            if (data != null) {
                val selectedUsers = data.getParcelableArrayListExtra<CalendarEmp>("selected_users")
                Log.d("AddCalendar", "Selected users received: $selectedUsers")
                    binding.chipGroup.removeAllViews()
                selectedUsers?.forEach { user ->
                    addChip(user)
                }
            } else {
                Log.d("AddCalendar", "Intent data is null")
            }
        }
    }

    private fun setupTimeList() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        for (i in 0 until 48) { // 24시간 = 48개의 30분 간격
            timeList.add(sdf.format(calendar.time))
            calendar.add(Calendar.MINUTE, 30)
        }
    }

    private fun setupTimeSpinners() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeList).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.startTime.adapter = adapter
        binding.endTime.adapter = adapter
    }

    private fun setupListeners() {
        binding.addButton.setOnClickListener {
            val intent = Intent(this, CalendarSelect::class.java)
            // ChipGroup에 있는 Chip들의 값을 가져와서 Intent에 추가
            val chips = getChipsFromChipGroup(binding.chipGroup)
            intent.putParcelableArrayListExtra("selected_users", ArrayList(chips))
            startActivityForResult(intent, REQUEST_CODE_SELECT_ATTENDEES)
        }

        // 메모 활성화 컬러
        binding.personalContactMemo.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                binding.memoLayout.setBoxStrokeColorStateList(ColorStateList.valueOf(Color.parseColor("#a97d6a")))
                binding.personalContactMemo.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#a97d6a")))
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.startDate.setOnClickListener {
            showDatePickerDialog { date -> binding.startDate.text = date }
        }

        binding.endDate.setOnClickListener {
            showDatePickerDialog { date -> binding.endDate.text = date }
        }

        binding.submit.setOnClickListener {
            var Datas: MutableList<participantDTO> = mutableListOf()
            for (user in selectedUsers) {
                var data = participantDTO(
                    id = user.id,
                    name = user.name,
                    department = user.department,
                )
                Datas.add(data)
            }
            val addData = CalendarDto(
                calendarTitle = binding.addTitle.text.toString(),
                calendarCreateAt = Calendar.getInstance().time.toString(),
                calendarContent = binding.personalContactMemo.text.toString(),
                calendarStartAt = "${binding.startDate.text}T${binding.startTime.selectedItem}",
                calendarEndAt = "${binding.endDate.text}T${binding.endTime.selectedItem}",
                attendees = Datas
            )
            // TODO: API 요청 코드 추가
        }
    }

    private fun initializeStartTime() {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentFormattedTime = sdf.format(Calendar.getInstance().time)
        val closestTimePosition = getClosestTimePosition(currentFormattedTime, sdf)

        if (closestTimePosition != -1) {
            binding.startTime.setSelection(closestTimePosition)
            binding.endTime.setSelection(closestTimePosition)
        }
    }

    private fun getClosestTimePosition(currentFormattedTime: String, sdf: SimpleDateFormat): Int {
        var closestPosition = -1
        var minDifference = Long.MAX_VALUE
        val currentDateTime = sdf.parse(currentFormattedTime)?.time ?: return -1

        timeList.forEachIndexed { index, time ->
            val timeInMillis = sdf.parse(time)?.time ?: return@forEachIndexed
            val difference = Math.abs(currentDateTime - timeInMillis)
            if (difference < minDifference) {
                minDifference = difference
                closestPosition = index
            }
        }
        return closestPosition
    }

    private fun showDatePickerDialog(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            onDateSelected(date)
        }, year, month, day).apply {
            datePicker.minDate = calendar.timeInMillis
            show()
        }
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun addChip(user: CalendarEmp) {
        val chip = Chip(this).apply {
            text = "${user.name} ${user.position}"
            isCloseIconVisible = true
            tag = user.id // 추가된 부분: 태그 설정
            setOnCloseIconClickListener {
                if (user.id != empId.toString()) {
                    binding.chipGroup.removeView(this)
                    user.isSelected = false
                } else {
                    Toast.makeText(this@AddCalendar, "본인은 삭제 할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        selectedUsers.add(user) // 추가된 부분: 선택된 사용자 리스트에 추가
        binding.chipGroup.addView(chip)
    }

    private fun getChipsFromChipGroup(chipGroup: ChipGroup): List<CalendarEmp> {
        val chips = mutableListOf<CalendarEmp>()
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            val userId = chip.tag as String
            val user = selectedUsers.find { it.id == userId }
            if (user != null) {
                chips.add(user)
            }
        }
        return chips
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val REQUEST_CODE_SELECT_ATTENDEES = 1
    }
}
