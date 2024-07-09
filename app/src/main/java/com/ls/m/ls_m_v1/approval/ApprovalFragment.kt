package com.ls.m.ls_m_v1.approval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ls.m.ls_m_v1.R

class ApprovalFragment : Fragment() {
    private val viewModel: ApprovalViewModel by viewModels()
    private lateinit var pendingDocumentsRecyclerView: RecyclerView
    private lateinit var rejectedDocumentsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_approval, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pendingDocumentsRecyclerView = view.findViewById(R.id.pendingDocumentsRecyclerView)
        rejectedDocumentsRecyclerView = view.findViewById(R.id.rejectedDocumentsRecyclerView)

        pendingDocumentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        rejectedDocumentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.pendingDocuments.observe(viewLifecycleOwner, Observer { approvals ->
            pendingDocumentsRecyclerView.adapter = ApprovalAdapter(approvals)
        })

        viewModel.rejectedDocuments.observe(viewLifecycleOwner, Observer { approvals ->
            rejectedDocumentsRecyclerView.adapter = ApprovalAdapter(approvals)
        })
    }
}