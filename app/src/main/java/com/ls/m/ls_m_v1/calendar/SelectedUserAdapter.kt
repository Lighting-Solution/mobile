package com.ls.m.ls_m_v1.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.calendar.entity.SelectedUser

class SelectedUserAdapter(
    private val users: List<SelectedUser>,
    private val listener: (SelectedUser) -> Unit
) : RecyclerView.Adapter<SelectedUserAdapter.UserViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectedUserAdapter.UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_calendar_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectedUserAdapter.UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
        holder.itemView.setOnClickListener { listener(user) }
        holder.checkBox.setOnCheckedChangeListener{ _, isChecked ->
            user.isSelected = isChecked
        }
    }

    override fun getItemCount(): Int = users.size

    class UserViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val nameTextView : TextView = itemView.findViewById(R.id.contact_name)
        private val positionTextView : TextView = itemView.findViewById(R.id.contact_position)
        val checkBox : CheckBox = itemView.findViewById(R.id.select_checkBox)

        fun bind(user: SelectedUser){
            nameTextView.text = user.name
            positionTextView.text = user.position
            checkBox.isChecked = user.isSelected
        }
    }
}