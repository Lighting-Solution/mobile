package com.ls.m.ls_m_v1.approval

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.approval.entity.ApprovalEmpDTO
import com.ls.m.ls_m_v1.approval.entity.ApprovalEntity

class ApprovalAdapter(
    var content: List<Pair<ApprovalEntity, ApprovalEmpDTO>>,
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_approver, parent, false)
        return ApprovalViewHolder(view)
    }

    override fun getItemCount() = content.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ApprovalViewHolder) {
            val (approval, emp) = content[position]
            holder.bind(approval, emp)
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

        fun bind(content: ApprovalEntity, approvalEmp: ApprovalEmpDTO) {
            approvalTitle.text = content.digitalApprovalName
            createAt.text = content.digitalApprovalCreateAt.toString()
            approvalState.text = if (content.drafterStatus == 1 && content.managerStatus == 1 && content.ceoStatus == 1) "true" else "false"
            name.text = approvalEmp.empName
            position.text = approvalEmp.position
        }
    }
}
