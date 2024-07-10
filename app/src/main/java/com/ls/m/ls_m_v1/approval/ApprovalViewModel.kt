package com.ls.m.ls_m_v1.approval

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ls.m.ls_m_v1.approval.entity.ApprovalEmpDTO
import com.ls.m.ls_m_v1.approval.entity.ApprovalEntity
import com.ls.m.ls_m_v1.approval.repository.ApprovalRepository
import com.ls.m.ls_m_v1.login.entity.LoginResponseDto
import com.ls.m.ls_m_v1.login.repository.LoginRepository
import kotlinx.coroutines.launch

class ApprovalViewModel(application: Application) : AndroidViewModel(application) {
    private val _pendingDocuments = MutableLiveData<List<Pair<ApprovalEntity, ApprovalEmpDTO>>>()
    val pendingDocuments: LiveData<List<Pair<ApprovalEntity, ApprovalEmpDTO>>> get() = _pendingDocuments

    private val _rejectedDocuments = MutableLiveData<List<Pair<ApprovalEntity, ApprovalEmpDTO>>>()
    val rejectedDocuments: LiveData<List<Pair<ApprovalEntity, ApprovalEmpDTO>>> get() = _rejectedDocuments

    private val approvalRepository = ApprovalRepository(application)
    private val loginRepository = LoginRepository(application)

    fun loadApprovals(loginData: LoginResponseDto) {

        viewModelScope.launch {
            val allApprovals = approvalRepository.getApprovalsForUser(loginData.empId)

            val pendingList = mutableListOf<Pair<ApprovalEntity, ApprovalEmpDTO>>()
            val rejectedList = mutableListOf<Pair<ApprovalEntity, ApprovalEmpDTO>>()

            allApprovals.forEach { (approval, emp) ->
                if (approval.digitalApprovalType == 1) {
                    if (loginData.positionId >= 3) {
                        rejectedList.add(Pair(approval, emp))
                    } else if (loginData.positionId == 1) {
                        if (approval.managerStatus == 1) {
                            rejectedList.add(Pair(approval, emp))
                        }
                    } else {
                        if (approval.managerStatus == 0) {
                            rejectedList.add(Pair(approval, emp))
                        }
                    }
                } else if (approval.ceoStatus == 0 && approval.digitalApprovalType == 0) {
                    if (loginData.positionId != 1) {
                        if (loginData.positionId == 2 && approval.managerStatus == 0){
                            pendingList.add(Pair(approval, emp))
                        }else if (loginData.positionId != 2){
                            pendingList.add(Pair(approval, emp))
                        }
                    }else if (loginData.positionId == 1 && approval.managerStatus == 1){
                        pendingList.add(Pair(approval, emp))
                    }
                }
            }

            _pendingDocuments.postValue(pendingList)
            _rejectedDocuments.postValue(rejectedList)
        }
    }
}
