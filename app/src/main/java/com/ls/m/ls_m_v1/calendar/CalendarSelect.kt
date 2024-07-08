package com.ls.m.ls_m_v1.calendar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.calendar.entity.SelectedUser
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.databinding.ActivityCalendarSelectBinding
import com.ls.m.ls_m_v1.emp.SelectedUserAdapter
import com.ls.m.ls_m_v1.emp.entity.EmpDTO
import com.ls.m.ls_m_v1.emp.repository.EmpRepository
import java.util.ArrayList

class CalendarSelect : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarSelectBinding
    private lateinit var empRepository: EmpRepository
    private lateinit var attendeeAdapter : SelectedUserAdapter

    private lateinit var empDTOList: List<EmpDTO>
    private lateinit var selectedUser: List<SelectedUser>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // EmpRepository 초기화
        empRepository = EmpRepository(this)

        empDTOList = empRepository.getAllEmps()
        selectedUser = empDTOList.map { empDto ->
            SelectedUser(
                id = empDto.empId.toString(),
                name = empDto.empName,
                position = empDto.position.positionName,
                department = empDto.department.departmentName,
                mobilePhone = empDto.empMP,
                isSelected = false
            )
        }
        binding.listPeopleRecyclerView.layoutManager = LinearLayoutManager(this)
        attendeeAdapter = SelectedUserAdapter(selectedUser){ user ->
            if (user.isSelected) {
                addChip(user)
            }else{
                removeChip(user)
            }
        }
        binding.listPeopleRecyclerView.adapter = attendeeAdapter

        binding.selectSubmit.setOnClickListener {
            val intent = Intent().apply {
                putParcelableArrayListExtra("selected_users", ArrayList(selectedUser.filter { it.isSelected }))
            }
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
    }

    private fun addChip(user: SelectedUser){
        val chip = Chip(this)
        chip.text = user.name
        chip.isCloseIconVisible = true
        chip.setOnClickListener{
            binding.chipGroup.removeView(it)
            user.isSelected = false
            attendeeAdapter.notifyDataSetChanged()
        }
        binding.chipGroup.addView(chip)
    }

    private fun removeChip(user: SelectedUser){
        val chip = binding.chipGroup.findViewWithTag<Chip>(user.name)
        if (chip != null){
            binding.chipGroup.removeView(chip)
        }
    }
}