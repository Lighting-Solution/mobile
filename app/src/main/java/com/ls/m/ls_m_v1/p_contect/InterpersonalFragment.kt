package com.ls.m.ls_m_v1.p_contect

import android.content.Intent
import android.os.Bundle
import android.os.DeadObjectException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.emp.ContactAdapter
import com.ls.m.ls_m_v1.emp.activity_contact_detail
import java.util.zip.Inflater

class InterpersonalFragment : Fragment() {
    private val personalViewModel: PersonalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        saveInstanceState: Bundle?
    ): View?{
        return inflater.inflate(R.layout.fragment_inter_personal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView : RecyclerView = view.findViewById(R.id.personalC_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        personalViewModel.contacts.observe(viewLifecycleOwner, Observer { contacts ->
            recyclerView.adapter = ContactAdapter(contacts) { contact ->
                val intent = Intent(requireContext(), activity_contact_detail::class.java).apply {
                    putExtra("id", contact.id)
                    putExtra("name", contact.name)
                    putExtra("email", contact.email)
                    putExtra("company", contact.company.companyName)
                    putExtra("department", contact.department)
                    putExtra("mobilePhone", contact.mobilePhone)
                    putExtra("officePhone", contact.company.companyNumber)
                    putExtra("position", contact.position)
                    putExtra("birthday", contact.birthday)
                    putExtra("buttonState", true) // 예시로 true 값 사용
                    putExtra("nickName", contact.nickname)
                    putExtra("memo", contact.memo)
                }
                startActivity(intent)
            }
        })
        try {
            // 폰트 요청 로직 추가
        } catch (e: DeadObjectException) {
            Log.e("PersonalFragment", "Content provider is dead", e)
        } catch (e: Exception) {
            Log.e("PersonalFragment", "Unexpected error", e)
        }

        val addButton = view.findViewById<TextView>(R.id.addPersonal)

        addButton.setOnClickListener {
            val intent = Intent(this.requireContext(), AddPersonal::class.java)
            startActivity(intent)
        }
    }
}