package com.ls.m.ls_m_v1.approval

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.approval.entity.ApprovalEmpDTO
import com.ls.m.ls_m_v1.approval.entity.ApprovalEntity
import com.ls.m.ls_m_v1.databinding.ActivityApprovalDetailBinding
import com.ls.m.ls_m_v1.emp.repository.EmpRepository
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class ApprovalDetail : AppCompatActivity() {
    private lateinit var binding: ActivityApprovalDetailBinding
    private lateinit var empRepository: EmpRepository
    private val approvalEmpList = mutableListOf<ApprovalEmpDTO>()
    private lateinit var approvalDetailAdapter: ApprovalDetailAdapter
    private lateinit var pdfView: PDFView

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityApprovalDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        pdfView = findViewById(R.id.pdfView)
        empRepository = EmpRepository(this)

        val approval = intent.getSerializableExtra("approval_data") as? ApprovalEntity
        approval?.let {
            binding.title.text = approval.digitalApprovalName
            empRepository.getDrafter(approval.drafterId)?.let { approvalEmpList.add(it) }
            empRepository.getManager(approval.drafterId)?.let { approvalEmpList.add(it) }
            empRepository.getCeo()?.let { approvalEmpList.add(it) }

            PDFBoxResourceLoader.init(applicationContext)

            approvalDetailAdapter = ApprovalDetailAdapter(approvalEmpList, approval)

            binding.approverRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@ApprovalDetail)
                adapter = approvalDetailAdapter
            }
        }

        binding.arrowIcon.setOnClickListener {
            if (binding.approverRecyclerView.visibility == View.GONE) {
                binding.approverRecyclerView.visibility = View.VISIBLE
                binding.arrowIcon.setImageResource(R.drawable.ic_arrow_up)
            } else {
                binding.approverRecyclerView.visibility = View.GONE
                binding.arrowIcon.setImageResource(R.drawable.ic_arrow_down)
            }
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        readPdf()
    }

//        private fun readPdf() {
//        lifecycleScope.launch {
//            try {
//                val dir = File(filesDir, "approval_files_2")
//                val pdfFile = File(dir, "sample.pdf")
//                Log.d("PdfActivity", "PDF 파일 경로: ${pdfFile.absolutePath}")
//
//                if (!pdfFile.exists()) {
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(this@ApprovalDetail, "PDF 파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                    return@launch
//                }
//
//                withContext(Dispatchers.IO) {
//                    val document = PDDocument.load(pdfFile)
//                    Log.d("ddd", "여기 까지 출력")
//                    document.close()
//
//                    withContext(Dispatchers.Main) {
//                        Log.d("dddd", "여기가 진짜 출력존")
//                        displayPdf(pdfFile)
//                    }
//                }
//            } catch (e: IOException) {
//                e.printStackTrace()
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@ApprovalDetail, "PDF 파일 읽기 실패", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

//    private fun displayPdf(file: File) {
//        pdfView.fromFile(file)
//            .enableSwipe(true)
//            .swipeHorizontal(false)
//            .enableAnnotationRendering(true)
//            .scrollHandle(null)
//            .load()
//    }

    private fun readPdf() {
        val dir = File(filesDir, "approval_files_2")
        val filePath = File(dir, "sample.pdf").absolutePath
        displayPdf(filePath)
    }

    private fun displayPdf(filePath: String) {
        try {
            val file = File(filePath)
            if (!file.exists()) {
                Toast.makeText(this, "PDF 파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                return
            }

            val fileDescriptor =
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(fileDescriptor)
            val pageCount = pdfRenderer.pageCount

            if (pageCount > 0) {
                val currentPage = pdfRenderer.openPage(0)
                val bitmap = Bitmap.createBitmap(
                    currentPage.width,
                    currentPage.height,
                    Bitmap.Config.ARGB_8888
                )
                currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                binding.pdfImageView.setImageBitmap(bitmap)
                currentPage.close()
            }

            pdfRenderer.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "PDF 파일을 읽는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }


}
