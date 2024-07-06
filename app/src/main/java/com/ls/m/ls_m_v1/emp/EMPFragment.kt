package com.ls.m.ls_m_v1.emp

import android.content.Intent
import android.os.Bundle
import android.os.DeadObjectException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ls.m.ls_m_v1.R

class EMPFragment : Fragment() {

    private val contactViewModel: ContactViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_e_m_p, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        contactViewModel.contacts.observe(viewLifecycleOwner, Observer { contacts ->
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
                    putExtra("buttonState", false) // 예시로 true 값 사용
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
    }
}

// 아이콘 누르면 intent하는거 하기
        // 데이터 베이스에 직접 데이터 넣어서 데이터 꺼내오는거 하기
        // DTO 만들지 고민할것


