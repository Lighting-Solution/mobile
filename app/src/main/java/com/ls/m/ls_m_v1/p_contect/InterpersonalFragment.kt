package com.ls.m.ls_m_v1.p_contect

import android.content.Intent
import android.os.Bundle
import android.os.DeadObjectException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.emp.ContactAdapter
import com.ls.m.ls_m_v1.emp.activity_contact_detail
import com.ls.m.ls_m_v1.login.repository.LoginRepository
import com.ls.m.ls_m_v1.p_contect.entity.ContanctAandroidDTO
import com.ls.m.ls_m_v1.p_contect.repository.PersonalContactRepository
import com.ls.m.ls_m_v1.p_contect.service.P_ContectService
import com.ls.m.ls_m_v1.p_contect.service.RetrofitInstancePersonal
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.zip.Inflater

class InterpersonalFragment : Fragment() {
    private val personalViewModel: PersonalViewModel by viewModels()
    private lateinit var p_contactService: P_ContectService
    private lateinit var loginRepository: LoginRepository
    private lateinit var personalContactRepository: PersonalContactRepository


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        saveInstanceState: Bundle?
    ): View?{
        return inflater.inflate(R.layout.fragment_inter_personal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        p_contactService = RetrofitInstancePersonal.api
        loginRepository = LoginRepository(requireContext())
        val loginData = loginRepository.getloginData()

        updatePersonal(loginData.empId, loginData.token)

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

    private fun updatePersonal(id: Int, token: String) {
        p_contactService.getP_ContectData("Bearer $token", id)
            .enqueue(object : Callback<ContanctAandroidDTO> {
                override fun onResponse(
                    call: Call<ContanctAandroidDTO>,
                    response: Response<ContanctAandroidDTO>
                ) {
                    if (response.isSuccessful) {
                        val personalData = response.body()
                        // 개인 주소록 데이터를 처리합니다.
                        personalData?.let {
                            // Company 데이터를 먼저 삽입
                            it.personalContactDTOList.forEach { contact ->
                                personalContactRepository.insertCompany(contact.company)
                            }
                            // PersonalContact 데이터를 삽입
                            it.personalContactDTOList.forEach { contact ->
                                personalContactRepository.insertPersonalContact(contact)
                            }
                            // PersonalGroup 데이터를 삽입
                            it.personalGroupDTOList.forEach { group ->
                                personalContactRepository.insertPersonalGroup(group)
                            }
                            // ContactGroup 데이터를 삽입
                            it.contactGroupDTOList.forEach { contactGroup ->
                                personalContactRepository.insertContactGroup(contactGroup)
                            }

                            Toast.makeText(requireContext(), "데이터 삽입 완료", Toast.LENGTH_SHORT)
                                .show()

                        }
                    } else {

                        Toast.makeText(requireContext(), "데이터 가져오기 실패", Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<ContanctAandroidDTO>, t: Throwable) {

                    Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT)
                        .show()

                }
            })
    }
}