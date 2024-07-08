package com.ls.m.ls_m_v1.calendar

import android.R
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.chip.Chip
import android.graphics.Color
import com.ls.m.ls_m_v1.calendar.entity.CalendarDto
import com.ls.m.ls_m_v1.calendar.entity.CalendarEntity
import com.ls.m.ls_m_v1.calendar.entity.SelectedUser
import com.ls.m.ls_m_v1.databinding.ActivityAddCalendarBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale
import java.util.zip.Inflater

class AddCalendar : AppCompatActivity() {
    private lateinit var binding : ActivityAddCalendarBinding
    private val timeList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 기본적으로 오늘 날짜를 설정
        val todayDate = getCurrentDate()
        binding.startDate.text = todayDate
        binding.endDate.text = todayDate

        binding.addButton.setOnClickListener {
            val intent = Intent(this@AddCalendar, CalendarSelect::class.java)
            startActivityForResult(intent, REQUEST_CODE_SELECT_ATTENDEES)
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

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 30)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // 24시간 동안의 시간을 배열에 추가

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

        // 24시간을 30분 간격으로 추가
        for (i in 0 until 48) { // 24시간 = 48개의 30 반격
            timeList.add(sdf.format(calendar.time))
            calendar.add(Calendar.MINUTE,30)
        }

        // ArrayAdapter를 사용하여 Spinner와 데이터를 연결
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, timeList)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.startTime.adapter = adapter
        binding.endTime.adapter = adapter

        val currentTime = Calendar.getInstance().time
        val currentFormattedTime = sdf.format(currentTime)

        // 가장 가까운 시간을 찾는 로직
        val closestTimePosition = getClosestTimePosition(currentFormattedTime, sdf)

        if (closestTimePosition != -1) {
            binding.startTime.setSelection(closestTimePosition)
            binding.endTime.setSelection(closestTimePosition)
        } else {
            // 디버그용 로그 출력
            Log.d("ddddd", currentTime.toString())
            Log.d("dddddddd", timeList.toString())
        }

        // Spinner에서 항목을 선택했을 때의 리스너 설정
        binding.startTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                // 선택된 항목에 대한 처리
                // 예시로 Toast 메시지를 사용하여 선택된 시간을 표시
                Toast.makeText(this@AddCalendar, "선택된 시간: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 항목도 선택되지 않았을 때의 처리
            }
        }

        // Spinner에서 항목을 선택했을 때의 리스너 설정
        binding.endTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                // 선택된 항목에 대한 처리
                // 예시로 Toast 메시지를 사용하여 선택된 시간을 표시
                Toast.makeText(this@AddCalendar, "선택된 시간: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 항목도 선택되지 않았을 때의 처리
            }
        }
        // CheckBox 상태 변화 감지하여 Spinner 활성화/비활성화
        binding.AlldayCheck.setOnCheckedChangeListener { _, isChecked ->
            binding.startTime.isEnabled = !isChecked
            binding.endTime.isEnabled = !isChecked
        }
        // 메모 활성화 컬러
        binding.personalContactMemo.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                binding.memoLayout.setBoxStrokeColorStateList(ColorStateList.valueOf(Color.parseColor("#a97d6a")))
                binding.personalContactMemo.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#a97d6a")))
            }
        }

        binding.submit.setOnClickListener {
            val addData = CalendarDto(
                calendarTitle = binding.addTitle.text.toString(),
                calendarCreateAt = LocalDateTime.now().toString(),
                calendarContent = binding.personalContactMemo.text.toString(),
                calendarStartAt = binding.startDate.text.toString() + "T" + binding.startTime.selectedItem.toString(),
                calendarEndAt = binding.endDate.text.toString() + "T" +binding.endTime.selectedItem.toString(),
//                user = CalendarDto(
//                )
            )

            if (!binding.startTime.isEnabled && !binding.endTime.isEnabled){
                addData.allDay = true
            }

            // 등록 데이터 api 요청




        }
    }

        private fun getClosestTimePosition(currentFormattedTime: String, sdf: SimpleDateFormat): Int {
            var closestPosition = -1
            var minDifference = Long.MAX_VALUE

            val currentDateTime = sdf.parse(currentFormattedTime)?.time ?: return -1

            for ((index, time) in timeList.withIndex()) {
                val timeInMillis = sdf.parse(time)?.time ?: continue
                val difference = Math.abs(currentDateTime - timeInMillis)

                if (difference < minDifference) {
                    minDifference = difference
                    closestPosition = index
                }
            }
            return closestPosition
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_ATTENDEES && resultCode == RESULT_OK) {
            val selectedUsers = data?.getParcelableArrayListExtra<SelectedUser>("selected_users")
            selectedUsers?.forEach { addChip(it) }
        }

    }

    private fun addChip(user: SelectedUser) {
        val chip = Chip(this)
        chip.text = user.name
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            binding.chipGroup.removeView(it)
        }
        binding.chipGroup.addView(chip)
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
        // 최소 날짜를 오늘 날짜로 설정하여 지난 날짜를 선택할 수 없게 함
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    companion object {
        const val REQUEST_CODE_SELECT_ATTENDEES = 1
    }
}