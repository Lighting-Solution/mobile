package com.ls.m.ls_m_v1.approval

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ls.m.ls_m_v1.approval.entity.ApprovalEmpDTO
import com.ls.m.ls_m_v1.approval.entity.ApprovalEntity
import com.ls.m.ls_m_v1.approval.repository.ApprovalRepository
import com.ls.m.ls_m_v1.login.repository.LoginRepository
import kotlinx.coroutines.launch

class ApprovalViewModel(application: Application) : AndroidViewModel(application) {
    private val _pendingDocuments = MutableLiveData<List<Pair<ApprovalEntity, ApprovalEmpDTO>>>()
    val pendingDocuments: LiveData<List<Pair<ApprovalEntity, ApprovalEmpDTO>>> get() = _pendingDocuments

    private val _rejectedDocuments = MutableLiveData<List<Pair<ApprovalEntity, ApprovalEmpDTO>>>()
    val rejectedDocuments: LiveData<List<Pair<ApprovalEntity, ApprovalEmpDTO>>> get() = _rejectedDocuments

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

            val pendingList = mutableListOf<Pair<ApprovalEntity, ApprovalEmpDTO>>()
            val rejectedList = mutableListOf<Pair<ApprovalEntity, ApprovalEmpDTO>>()

            allApprovals.forEach { (approval, emp) ->
//                if (approval.managerStatus == 0 && emp.position == "대표이사") {
//                    // Manager status is false and the user is CEO, skip this document
//                    return@forEach
//                }
//                if (approval.ceoRejectAt != null && emp.position == "부장") {
//                    // CEO has rejected the document and the user is Manager, skip this document
//                    return@forEach
//                }

                if (approval.digitalApprovalType == 1) {
                    rejectedList.add(Pair(approval, emp))
                } else {
                    pendingList.add(Pair(approval, emp))
                }
            }

            _pendingDocuments.postValue(pendingList)
            _rejectedDocuments.postValue(rejectedList)
        }
    }
}
