package com.truedigital.features.truecloudv3.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Space
import androidx.recyclerview.widget.RecyclerView
import com.newrelic.agent.android.NewRelic
import com.tdg.truecloud.databinding.TrueCloudv3ViewholderContactHeaderBinding
import com.tdg.truecloud.databinding.TrueCloudv3ViewholderContactItemBinding
import com.truedigital.features.truecloudv3.domain.model.Contact
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.domain.model.HeaderSelectionModel

class ContactAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var contactList: MutableList<Contact> = mutableListOf()
    var onItemClicked: ((ContactTrueCloudModel) -> Unit)? = null

    companion object {
        private const val CONTACT_HEADER_VIEW_TYPE = 0
        private const val CONTACT_ITEM_VIEW_TYPE = 1
        private const val UNKNOWN_VIEW_TYPE = -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CONTACT_HEADER_VIEW_TYPE -> {
                ContactHeaderViewHolder(
                    TrueCloudv3ViewholderContactHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            CONTACT_ITEM_VIEW_TYPE -> {
                ContactViewHolder(
                    TrueCloudv3ViewholderContactItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onItemClicked
                )
            }

            else -> {
                NewRelic.recordHandledException(
                    Exception("View type $viewType isn't supported in AddressBookContactAdapter")
                )
                return object : RecyclerView.ViewHolder(Space(parent.context)) {}
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        contactList[position].let { item ->
            when (holder) {
                is ContactHeaderViewHolder -> {
                    (item as HeaderSelectionModel).let { holder.bind(it) }
                }

                is ContactViewHolder -> {
                    (item as ContactTrueCloudModel).let { holder.bind(it) }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (contactList[position]) {
            is HeaderSelectionModel -> CONTACT_HEADER_VIEW_TYPE
            is ContactTrueCloudModel -> CONTACT_ITEM_VIEW_TYPE
            else -> UNKNOWN_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    fun getItemAtPosition(position: Int): Contact {
        return contactList[position]
    }

    fun setContactList(list: List<Contact>) {
        this.contactList.clear()
        this.contactList.addAll(list)
        notifyDataSetChanged()
    }
}
