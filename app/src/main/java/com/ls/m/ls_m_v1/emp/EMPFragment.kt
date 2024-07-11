package com.ls.m.ls_m_v1.emp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.emp.entity.AllContact

class EMPFragment : Fragment() {

    private lateinit var contactViewModel: ContactViewModel
    private lateinit var contactAdapter: ContactAdapter

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

        // ViewModel 초기화
        contactViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )
            .get(ContactViewModel::class.java)

        // 어댑터 초기화
        contactAdapter = ContactAdapter(emptyList()) { contact ->
            if (contact is AllContact) {
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
                    putExtra("buttonState", false)
                }
                startActivity(intent)
            }
        }
        recyclerView.adapter = contactAdapter

        // ViewModel 관찰
        contactViewModel.contacts.observe(viewLifecycleOwner, Observer { contacts ->
            contactAdapter.updateContacts(contacts)
        })
    }
}