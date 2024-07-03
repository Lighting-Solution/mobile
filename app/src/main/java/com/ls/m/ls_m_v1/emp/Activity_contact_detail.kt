package com.ls.m.ls_m_v1.emp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.databinding.ActivityContactDetailBinding

class activity_contact_detail : AppCompatActivity() {
    private lateinit var binding : ActivityContactDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        val company = intent.getStringExtra("company")
        val department = intent.getStringExtra("department")
        val mobilePhone = intent.getStringExtra("mobilePhone")
        val officePhone = intent.getStringExtra("officePhone")
        val position = intent.getStringExtra("position")
        val birthday = intent.getStringExtra("birthday")

        binding.contactName.text = "${name} ${position}"
        binding.contactEmail.text = email
        binding.company.text = "회사: $company"
        binding.department.text = "부서: $department"
        binding.mobilePhone.text = "휴대폰: $mobilePhone"
        binding.officePhone.text = "직통전화: $officePhone"
        binding.position.text = "직급: $position"
        binding.birthday.text = "생일: $birthday"
    }
}