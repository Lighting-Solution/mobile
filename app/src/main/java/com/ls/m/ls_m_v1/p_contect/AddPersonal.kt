package com.ls.m.ls_m_v1.p_contect

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.p_contect.dao.AddPersonalDAOImpl
import com.ls.m.ls_m_v1.p_contect.entity.PersonalGroupDTO

class AddPersonal : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_personal)

        val spinner: Spinner = findViewById(R.id.spinner)
        val companyInfoLayout: LinearLayout = findViewById(R.id.companyInfoLayout)


        val personalDao = AddPersonalDAOImpl(this)
        val companys = personalDao.getAllCompanyData()

        val items = companys.map { it.companyName }.toMutableList()
        items.add(0, "Select Option")

        //Arrayabapter 를 사용하여  spinner에 항목을 설정합니다.
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.adapter = adapter

        // Spinner의 항목이 선택 되었을때 동작을 설정합니다.
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position) as String
                // 회사가 선택되면 회사 정보 레이아웃을 표시하고, 그렇지 않으면 숨김
                if (selectedItem != "Select Option") {
                    companyInfoLayout.visibility = View.INVISIBLE
                } else {
                    companyInfoLayout.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        val backButton = findViewById<ImageView>(R.id.backButton_1)
        backButton.setOnClickListener {
            finish()
        }
        val submit = findViewById<TextView>(R.id.submit)
        submit.setOnClickListener {


        }
    }


}
