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
import com.ls.m.ls_m_v1.emp.ContactViewModel

class EMPFragment : Fragment() {

    private val contactViewModel: ContactViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_e_m_p, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        contactViewModel.contacts.observe(viewLifecycleOwner, Observer { contacts ->
            recyclerView.adapter = ContactAdapter(contacts)
        })
        try {
            // 폰트 요청 로직 추가
        } catch (e: DeadObjectException) {
            Log.e("CalendarFragment", "Content provider is dead", e)
        } catch (e: Exception) {
            Log.e("CalendarFragment", "Unexpected error", e)
        }

        // 아이콘 누르면 intent하는거 하기
        // 데이터 베이스에 직접 데이터 넣어서 데이터 꺼내오는거 하기
        // DTO 만들지 고민할것
    }
}
