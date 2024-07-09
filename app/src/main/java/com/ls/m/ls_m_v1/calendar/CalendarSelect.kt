package com.ls.m.ls_m_v1.calendar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.ls.m.ls_m_v1.databinding.ActivityCalendarSelectBinding
import com.ls.m.ls_m_v1.emp.SelectCalendarViewModel
import com.ls.m.ls_m_v1.emp.SelectedUserAdapter
import com.ls.m.ls_m_v1.calendar.entity.CalendarEmp
import com.ls.m.ls_m_v1.login.repository.LoginRepository
import kotlinx.coroutines.launch

class CalendarSelect : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarSelectBinding
    private val viewModel: SelectCalendarViewModel by viewModels()
    private lateinit var attendeeAdapter: SelectedUserAdapter
    private val selectedUsers = ArrayList<CalendarEmp>()
    private lateinit var loginRepository: LoginRepository
    private var empId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이전 Activity로부터 전달된 데이터 가져오기
        val previousSelectedUsers =
            intent.getParcelableArrayListExtra<CalendarEmp>("selected_users")
        if (previousSelectedUsers != null) {
            selectedUsers.addAll(previousSelectedUsers)
        }

        lifecycleScope.launch {
            empId = getLoginData()
        }
        binding.listPeopleRecyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.contacts.observe(this) { contacts ->
            attendeeAdapter = SelectedUserAdapter(contacts) { user ->
                updateSelectedUsers(user)
                if (user.isSelected) {
                    addChip(user)
                } else {
                    removeChip(user)
                }
            }
            binding.listPeopleRecyclerView.adapter = attendeeAdapter

            // 초기 상태 설정: 이전에 선택된 사용자들을 체크박스에서 선택된 상태로 표시
            previousSelectedUsers?.forEach { user ->
                addChip(user)
                val index = contacts.indexOfFirst { it is CalendarEmp && it.id == user.id }
                if (index >= 0) {
                    (contacts[index] as CalendarEmp).isSelected = true
                }
            }
            attendeeAdapter.notifyDataSetChanged()
        }

        binding.selectSubmit.setOnClickListener {
            Log.d("CalendarSelect", "Selected users: $selectedUsers")
            val intent = Intent().apply {
                putParcelableArrayListExtra("selected_users", selectedUsers)
            }
            setResult(Activity.RESULT_OK, intent)
            Log.d("CalendarSelect", "Intent set with selected users")
            finish()
        }
        binding.backButton.setOnClickListener {
            finish()
        }

    }

    private suspend fun getLoginData(): Int {
        loginRepository = LoginRepository(this)
        val value = loginRepository.getloginData()
        return value.empId
    }

    private fun addChip(user: CalendarEmp) {
        val chip = Chip(this).apply {
            text = "${user.name} ${user.position}"
            isCloseIconVisible = true
            tag = user.id // 추가된 부분: 태그 설정
            setOnCloseIconClickListener {
                if (user.id != empId.toString()) {
                    binding.chipGroupSelectedView.removeView(this)
                    user.isSelected = false
                    updateSelectedUsers(user)
                }
            }
        }
        binding.chipGroupSelectedView.addView(chip)
    }

    private fun removeChip(user: CalendarEmp) {
        val chip =
            binding.chipGroupSelectedView.findViewWithTag<Chip>(user.id)  // 수정된 부분: 태그로 Chip 찾기
        if (chip != null) {
            binding.chipGroupSelectedView.removeView(chip)
        }
    }

    private fun updateSelectedUsers(user: CalendarEmp) {
        if (user.isSelected) {
            if (!selectedUsers.contains(user)) {
                selectedUsers.add(user)
            }
        } else {
            selectedUsers.remove(user)
        }
        Log.d("CalendarSelect", "Updated selected users: $selectedUsers") // 추가된 로그
    }
}
