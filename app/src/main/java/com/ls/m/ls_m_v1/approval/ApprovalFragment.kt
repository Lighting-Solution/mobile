package com.ls.m.ls_m_v1.approval

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.approval.service.downloadAndSavePDF
import com.ls.m.ls_m_v1.login.repository.LoginRepository
import kotlinx.coroutines.launch
import java.io.Serializable

class ApprovalFragment : Fragment() {

    private val approvalViewModel: ApprovalViewModel by viewModels()

    private lateinit var pendingRecyclerView: RecyclerView
    private lateinit var rejectedRecyclerView: RecyclerView

    private lateinit var pendingAdapter: ApprovalAdapter
    private lateinit var rejectedAdapter: ApprovalAdapter

    private lateinit var loginRepository: LoginRepository

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

        loginRepository = LoginRepository(requireContext())

        // loginData를 가져온 후에 어댑터를 설정하고 observeViewModel 호출
        lifecycleScope.launch {
            val loginData = loginRepository.getloginData()

            pendingAdapter = ApprovalAdapter(emptyList(), loginData) { approval ->
//                lifecycleScope.launch {
//                    val success = downloadAndSavePDF(requireContext(), approval.digitalApprovalId)
//                    if (success) {
//                        Toast.makeText(requireContext(), "PDF 다운로드 성공", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(requireContext(), "PDF 다운로드 실패", Toast.LENGTH_SHORT).show()
//                    }
//                }
                val intent = Intent(requireContext(), ApprovalDetail::class.java).apply {
                    putExtra("approval_data", approval as Serializable)
                }
                startActivity(intent)

            }
            rejectedAdapter = ApprovalAdapter(emptyList(), loginData) { approval ->
                lifecycleScope.launch {
                    val success = downloadAndSavePDF(requireContext(), approval.digitalApprovalId)
                    if (success) {
                        Toast.makeText(requireContext(), "PDF 다운로드 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "PDF 다운로드 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            pendingRecyclerView.adapter = pendingAdapter
            rejectedRecyclerView.adapter = rejectedAdapter

            approvalViewModel.loadApprovals(loginData)
            observeViewModel()
        }
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
