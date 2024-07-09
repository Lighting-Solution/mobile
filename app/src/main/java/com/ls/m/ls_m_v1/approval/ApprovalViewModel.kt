package com.ls.m.ls_m_v1.approval

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ls.m.ls_m_v1.approval.entity.ApprovalEmpDTO
import com.ls.m.ls_m_v1.approval.entity.ApprovalEntity
import com.ls.m.ls_m_v1.approval.repository.ApprovalRepository
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.emp.repository.EmpRepository
import com.ls.m.ls_m_v1.login.repository.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApprovalViewModel(application: Application) : AndroidViewModel(application) {
    private val _pendingDocuments = MutableLiveData<List<ApprovalEntity>>()
    val pendingDocuments: LiveData<List<ApprovalEntity>> get() = _pendingDocuments

    private val _rejectedDocuments = MutableLiveData<List<ApprovalEntity>>()
    val rejectedDocuments: LiveData<List<ApprovalEntity>> get() = _rejectedDocuments

    private val approvalRepository = ApprovalRepository(application)
    private val loginRepository = LoginRepository(application)

    init {
        loadApprovals()
    }

    private fun loadApprovals() {
        viewModelScope.launch {
            val loginData = loginRepository.getloginData()
            val empId = loginData.empId
            val allApprovals = approvalRepository.getApprovalsForUser(empId)
            _pendingDocuments.postValue(allApprovals.filter { it.digitalApprovalType == 1 })
            _rejectedDocuments.postValue(allApprovals.filter { it.digitalApprovalType == 0 })
        }
    }
}