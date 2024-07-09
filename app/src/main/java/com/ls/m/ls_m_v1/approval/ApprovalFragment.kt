package com.ls.m.ls_m_v1.approval

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ls.m.ls_m_v1.R

class ApprovalFragment : Fragment() {

    private val approvalViewModel: ApprovalViewModel by viewModels()

    private lateinit var pendingRecyclerView: RecyclerView
    private lateinit var rejectedRecyclerView: RecyclerView

    private lateinit var pendingAdapter: ApprovalAdapter
    private lateinit var rejectedAdapter: ApprovalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_approval, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pendingRecyclerView = view.findViewById(R.id.pendingDocumentsRecyclerView)
        rejectedRecyclerView = view.findViewById(R.id.rejectedDocumentsRecyclerView)

        pendingRecyclerView.layoutManager = LinearLayoutManager(context)
        rejectedRecyclerView.layoutManager = LinearLayoutManager(context)

        pendingAdapter = ApprovalAdapter(emptyList()) { approval ->
            // 클릭 리스너 설정
        }
        rejectedAdapter = ApprovalAdapter(emptyList()) { approval ->
            // 클릭 리스너 설정
        }

        pendingRecyclerView.adapter = pendingAdapter
        rejectedRecyclerView.adapter = rejectedAdapter

        observeViewModel()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        approvalViewModel.pendingDocuments.observe(viewLifecycleOwner) { pendingDocuments ->
            pendingAdapter.content = pendingDocuments
            pendingAdapter.notifyDataSetChanged()
        }

        approvalViewModel.rejectedDocuments.observe(viewLifecycleOwner) { rejectedDocuments ->
            rejectedAdapter.content = rejectedDocuments
            rejectedAdapter.notifyDataSetChanged()
        }
    }
}
