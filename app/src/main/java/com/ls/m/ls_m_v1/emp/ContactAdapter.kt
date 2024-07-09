package com.ls.m.ls_m_v1.emp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ls.m.ls_m_v1.R
import com.ls.m.ls_m_v1.emp.entity.AllContact
import com.ls.m.ls_m_v1.emp.entity.SectionHeader

class ContactAdapter(
    private var contacts: List<Any>,
    private val clickListener: (AllContact) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SECTION = 0
        private const val VIEW_TYPE_CONTACT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (contacts[position] is SectionHeader) VIEW_TYPE_SECTION else VIEW_TYPE_CONTACT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SECTION) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_section_header, parent, false)
            SectionViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
            ContactViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SectionViewHolder) {
            val sectionHeader = contacts[position] as SectionHeader
            holder.bind(sectionHeader)
        } else if (holder is ContactViewHolder) {
            val contact = contacts[position] as AllContact
            holder.bind(contact)
            holder.itemView.setOnClickListener {
                clickListener(contact)
            }
        }
    }

    override fun getItemCount() = contacts.size

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.contact_name)
        private val positionTextView: TextView = itemView.findViewById(R.id.contact_position)

        fun bind(contact: AllContact) {
            nameTextView.text = contact.name
            positionTextView.text = "${contact.position} / ${contact.department}"
        }
    }

    class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sectionTextView: TextView = itemView.findViewById(R.id.section_header)

        fun bind(sectionHeader: SectionHeader) {
            sectionTextView.text = sectionHeader.title
        }
    }
}
