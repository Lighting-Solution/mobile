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
    private val approvalViewModel : ApprovalViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_approval, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView : RecyclerView = view.findViewById(R.id.pendingDocumentsRecyclerView)
        val rejectRecyclerView : RecyclerView = view.findViewById(R.id.rejectedDocumentsRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)

        approvalViewModel.contents.observe(viewLifecycleOwner, Observer {items ->
            recyclerView.adapter = ApprovalAdapter(items){item ->
//                val intent = Intent(requireContext(), ApprovalDetail::class.java, false)

            }
        })


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}