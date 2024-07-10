package com.ls.m.ls_m_v1.approval

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.approval.entity.ApprovalEmpDTO
import com.ls.m.ls_m_v1.approval.entity.ApprovalEntity
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

class ApprovalDetailAdapter(private val dataList: List<ApprovalEmpDTO>, private var approvalData: ApprovalEntity) :
    RecyclerView.Adapter<ApprovalDetailAdapter.ApprovalDetailViewHolder>() {
    class ApprovalDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val empName: TextView = itemView.findViewById(R.id.empName)
        val department: TextView = itemView.findViewById(R.id.departmentName)
//        val approvalDate: TextView = itemView.findViewById(R.id.approvalDate)
        val approvalState: TextView = itemView.findViewById(R.id.approvalState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ApprovalDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detail_approval, parent, false)
        return ApprovalDetailViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ApprovalDetailViewHolder,
        position: Int
    ) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd(E) HH:mm", Locale.getDefault())
        val data = dataList[position]
        holder.empName.text = "${data.empName} ${data.position}"
        holder.department.text = data.department
        if (data.position == "부장"){
            if (approvalData.managerStatus == 1){
                holder.approvalState.text = "결재 완료"
            }else if (approvalData.managerStatus == 0){
                holder.approvalState.text = "결재 대기"
            }
        }else if (data.position == "대표이사"){
            if (approvalData.managerStatus == 1 && approvalData.ceoStatus == 0  ){
                holder.approvalState.text = "결재 대기"
            }else if (approvalData.managerStatus == 0 && approvalData.ceoStatus == 0){
                holder.approvalState.text = "결재 예정"
            }
        }else{
            holder.approvalState.text = "결재 완료"
        }
    }

    override fun getItemCount() = dataList.size


}