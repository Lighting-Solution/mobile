package com.ls.m.ls_m_v1.emp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ls.m.ls_m_v1.databinding.ActivityContactDetailBinding
import com.ls.m.ls_m_v1.p_contect.ModifyPersonal

class activity_contact_detail : AppCompatActivity() {
    private lateinit var binding : ActivityContactDetailBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        val company = intent.getStringExtra("company")
        val department = intent.getStringExtra("department")
        val mobilePhone = intent.getStringExtra("mobilePhone")
        val officePhone = intent.getStringExtra("officePhone")
        val position = intent.getStringExtra("position")
        val birthday = intent.getStringExtra("birthday")
        val buttonState = intent.getBooleanExtra("buttonState", false)
        val nickName = intent.getStringExtra("nickName")
        val memo = intent.getStringExtra("memo")

        binding.contactName.text = "${name} ${position}"
        binding.contactEmail.text = email
        binding.company.text = "회사: $company"
        binding.department.text = "부서: $department"
        binding.mobilePhone.text = "휴대폰: $mobilePhone"
        binding.officePhone.text = "직통전화: $officePhone"
        binding.position.text = "직급: $position"
        binding.birthday.text = "생일: ${birthday ?: "-"}"

        binding.backButton.setOnClickListener {
            finish()
        }
        binding.callIc.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${mobilePhone}"))
            startActivity(intent)
        }
        binding.emailIc.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
            startActivity(intent)
        }
        binding.messageIc.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${mobilePhone}"))
            startActivity(intent)
        }
        binding.chatIc.setOnClickListener {
            // 대화하기 클릭 리스너 설정 (예: 채팅 앱으로 이동)
        }

        if (!buttonState){
            // 편집 버튼 안보이게 하기
            binding.modifyButton.visibility = View.INVISIBLE
        }
        binding.modifyButton.setOnClickListener {
//            // 삭제 하는 구현
//            val deletePersonal  = id?.let { it1 ->
//                DeletePersonalDTO(
//                    personalContactId = it1.toInt()
//                )
//            }
            val nextIntent = Intent(this, ModifyPersonal::class.java).apply {
                putExtra("id", id)
                putExtra("name", name)
                putExtra("email", email)
                putExtra("company", company)
                putExtra("department", department)
                putExtra("mobilePhone", mobilePhone)
                putExtra("officePhone", officePhone)
                putExtra("position", position)
                putExtra("birthday", birthday)
                putExtra("nickName", nickName)
                putExtra("memo", memo)
            }
            startActivity(nextIntent)
        }
    }
}