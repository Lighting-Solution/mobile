package com.ls.m.ls_m_v1.approval


import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.approval.entity.ApprovalEmpDTO
import com.ls.m.ls_m_v1.approval.entity.ApprovalEntity
import com.ls.m.ls_m_v1.login.entity.LoginResponseDto

class ApprovalAdapter(
    var content: List<Pair<ApprovalEntity, ApprovalEmpDTO>>,
    private var loginData: LoginResponseDto,
    private val clickListener: (ApprovalEntity) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PENDING = 0
        private const val VIEW_TYPE_REJECTED = 1
    }

    override fun getItemViewType(position: Int): Int {
        val (approval, _) = content[position]
        return if (approval.digitalApprovalType == 1) VIEW_TYPE_REJECTED else VIEW_TYPE_PENDING
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_approver, parent, false)
        return ApprovalViewHolder(view)
    }

    override fun getItemCount() = content.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ApprovalViewHolder) {
            val (approval, emp) = content[position]
            holder.bind(approval, emp, loginData)
            holder.itemView.setOnClickListener {
                clickListener(approval)
            }
        }
    }

    class ApprovalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val approvalTitle: TextView = view.findViewById(R.id.approvalTitle)
        private val approvalState: TextView = view.findViewById(R.id.approvalState)
        private val createAt: TextView = view.findViewById(R.id.createdAt)
        private val name: TextView = view.findViewById(R.id.name)
        private val position: TextView = view.findViewById(R.id.position)
        private val viewColor: ImageView = view.findViewById(R.id.viewColor)

        fun bind(
            content: ApprovalEntity,
            approvalEmp: ApprovalEmpDTO,
            loginData: LoginResponseDto
        ) {


            if (!(content.drafterStatus == 1 && content.managerStatus == 1 && content.ceoStatus == 1)) {
                // 기안자 이름
                name.text = approvalEmp.empName
                // 기안자 직급
                position.text = approvalEmp.position
                // 결재 서류 이름
                approvalTitle.text = content.digitalApprovalName

                if (content.digitalApprovalType == 0) {
                    createAt.text = content.digitalApprovalCreateAt
                    viewColor.imageTintList =
                        ContextCompat.getColorStateList(itemView.context, R.color.orange)
                    if (loginData.positionId >= 3) {
                        approvalState.text =
                            if (content.managerStatus == 1) "[대표이사] 결재 대기" else "[부장] 결재 대기"
                    } else if (loginData.positionId == 2) {
                        approvalState.text =
                            if (content.managerStatus == 1) "[대표이사] 결재 대기" else "[${approvalEmp.position}] 결재 요청"
                    } else if (loginData.positionId == 1) {
                        approvalState.text = if (content.managerStatus == 1) "최종 결재 대기" else null
                    }

                } else {
                    viewColor.imageTintList =
                        ContextCompat.getColorStateList(itemView.context, R.color.red)
                    createAt.text =
                        if (content.ceoRejectAt != null) "반려일 ${content.ceoRejectAt}" else if (content.managerRejectAt != null) "반려일 ${content.managerRejectAt}" else null
                }
            }

        }
    }
}
