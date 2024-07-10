package com.ls.m.ls_m_v1.approval

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.approval.entity.ApprovalEmpDTO
import com.ls.m.ls_m_v1.approval.entity.ApprovalEntity
import com.ls.m.ls_m_v1.approval.service.RetrofitInstanceApproval
import com.ls.m.ls_m_v1.databaseHelper.DatabaseHelper
import com.ls.m.ls_m_v1.databinding.ActivityApprovalDetailBinding
import com.ls.m.ls_m_v1.emp.repository.EmpRepository
import com.ls.m.ls_m_v1.login.repository.LoginRepository
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory
import com.tom_roush.pdfbox.util.Matrix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class ApprovalDetail : AppCompatActivity() {
    private lateinit var binding: ActivityApprovalDetailBinding
    private lateinit var empRepository: EmpRepository
    private val approvalEmpList = mutableListOf<ApprovalEmpDTO>()
    private lateinit var approvalDetailAdapter: ApprovalDetailAdapter
    private lateinit var loginRepository: LoginRepository
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityApprovalDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        empRepository = EmpRepository(this)
        dbHelper = DatabaseHelper(this)

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
            readPdf(approval.digitalApprovalId)
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

        binding.approvalButton.setOnClickListener {
            // 이 버튼을 눌렀을때 filesDir/signfile에 저장된 이미지를 꺼내서 pdf에 얹어서 다시 같은이름으로 저장
            approval?.let {
                addImageToPdf(it)

                loginRepository = LoginRepository(this)

                val loginData = loginRepository.getloginData()
                if (loginData.positionId == 2) {
                    approval.managerStatus = 1
                } else if (loginData.positionId == 1) {
                    approval.ceoStatus = 1
                }
                // PDF 파일과 변수들을 API로 전송
                val pdfFile =
                    File(filesDir, "approval_files/signed${approval.digitalApprovalId}.pdf")
                uploadPdfToServer(pdfFile, approval)

                // 목록으로 가면서 리프레쉬


            }
        }

    }

    private fun readPdf(id: Int) {
        lifecycleScope.launch {
            try {
                val dir = File(filesDir, "approval_files")
                val pdfFile = File(dir, "signed${id}.pdf")
                Log.d("PdfActivity", "PDF 파일 경로: ${pdfFile.absolutePath}")

                if (!pdfFile.exists()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@ApprovalDetail,
                            "PDF 파일이 존재하지 않습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    val document = PDDocument.load(pdfFile)
                    document.close()

                    withContext(Dispatchers.Main) {
                        displayPdf(pdfFile)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ApprovalDetail, "PDF 파일 읽기 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayPdf(file: File) {
        binding.pdfView.fromFile(file)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableAnnotationRendering(true)
            .scrollHandle(null)
            .load()
    }

    private fun addImageToPdf(approvalData: ApprovalEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            val dir = File(filesDir, "approval_files")
            val pdfFile = File(dir, "signed${approvalData.digitalApprovalId}.pdf")
            val updatedPdfFile = File(dir, "signed${approvalData.digitalApprovalId}.pdf")

            if (!pdfFile.exists()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ApprovalDetail, "PDF 파일이 존재하지 않습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
                return@launch
            }

            try {
                val document = PDDocument.load(pdfFile)
                val imageFile = File(filesDir, "signfile/signature.png")

                if (!imageFile.exists()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@ApprovalDetail,
                            "이미지 파일이 존재하지 않습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    document.close()
                    return@launch
                }

                val image = BitmapFactory.decodeFile(imageFile.absolutePath)
                val pdImage = LosslessFactory.createFromImage(document, image)

                val page = document.getPage(0)
                val contentStream =
                    PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)

                // 이미지의 위치와 크기를 결정합니다.
                val (x, y) = when (approvalData.managerStatus) {
                    0 -> Pair(550f, 47f)  // managerStatus 0일 때 위치
                    1 -> Pair(630f, 47f)  // managerStatus 1일 때 위치
                    else -> Pair(470f, 47f)  // 기본 위치
                }
                val imageWidth = 40f
                val imageHeight = 35f

                // 이미지 변환 매트릭스를 사용하여 이미지를 올바르게 배치합니다.
                val matrix = Matrix(imageWidth, 0f, 0f, -imageHeight, x, y + imageHeight)

                // PDFBox의 PDPageContentStream을 사용하여 이미지를 그립니다.
                contentStream.drawImage(pdImage, matrix)

                // PDFBox의 PDPageContentStream을 사용하여 이미지를 그립니다.
                contentStream.drawImage(pdImage, matrix)
                contentStream.close()

                document.save(updatedPdfFile)
                document.close()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ApprovalDetail, "PDF 파일 저장 완료", Toast.LENGTH_SHORT).show()
                    displayPdf(updatedPdfFile)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ApprovalDetail, "PDF 파일 저장 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // PDF 파일과 변수들을 서버로 전송하는 함수
    private fun uploadPdfToServer(pdfFile: File, approvalEntity: ApprovalEntity) {
        if (!pdfFile.exists()) {
            Toast.makeText(this, "PDF 파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val pdfRequestBody = RequestBody.create("application/pdf".toMediaTypeOrNull(), pdfFile)
        val pdfPart = MultipartBody.Part.createFormData("pdf", pdfFile.name, pdfRequestBody)

        val gson = Gson()
        val approvalEntityJson = gson.toJson(approvalEntity)
        val approvalEntityBody =
            RequestBody.create("application/json".toMediaTypeOrNull(), approvalEntityJson)

        val call = RetrofitInstanceApproval.instance.uploadPdf(pdfPart, approvalEntityBody)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ApprovalDetail, "PDF 업로드 성공", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ApprovalDetail, "PDF 업로드 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ApprovalDetail, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })

    }
}
