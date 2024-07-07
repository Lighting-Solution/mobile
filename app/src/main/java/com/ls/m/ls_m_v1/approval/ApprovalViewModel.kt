package com.ls.m.ls_m_v1.approval

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ls.m.ls_m_v1.approval.entity.ApprovalEmpDTO
import com.ls.m.ls_m_v1.approval.entity.ApprovalEntity
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper

class ApprovalViewModel(application: Application) : AndroidViewModel(application) {
    private val _contents = MutableLiveData<List<Pair<ApprovalEntity, ApprovalEmpDTO>>>()

    val contents: LiveData<List<Pair<ApprovalEntity, ApprovalEmpDTO>>> get() = _contents

    private val dbHelper = DatabaseHelper(application)

    init {
        loadData()
    }

    private fun loadData() {
        val approvalWithEmp = dbHelper.getApprovalWithEMPDrafter()
        val filteredData = approvalWithEmp.filter { it.first.ceoStatus == false }
        _contents.value = filteredData
    }
}
