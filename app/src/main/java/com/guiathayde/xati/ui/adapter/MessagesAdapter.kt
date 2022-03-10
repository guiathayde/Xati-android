package com.guiathayde.xati.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guiathayde.xati.databinding.AdapterRecyclerSelectedUserMessageBinding
import com.guiathayde.xati.databinding.AdapterRecyclerUserMessageBinding
import com.guiathayde.xati.model.Message
import com.guiathayde.xati.service.SavedPreference


class MessagesAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SELECTED_USER = 12
    private val USER = 13

    private lateinit var savedPreference: SavedPreference

    var messageList = mutableListOf<Message>()

    override fun getItemViewType(position: Int): Int {
        savedPreference = SavedPreference(context)
        val userId = savedPreference.getUserId()
        return if (messageList.elementAt(position).userId == userId) {
            USER
        } else {
            SELECTED_USER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        if (viewType == USER) {
            return UserMessageViewHolder(
                AdapterRecyclerUserMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return SelectedUserMessageViewHolder(
                AdapterRecyclerSelectedUserMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UserMessageViewHolder) {
            holder.binding.textMessage.text = messageList[position].text
        } else if (holder is SelectedUserMessageViewHolder) {
            holder.binding.textMessage.text = messageList[position].text
        }
    }

    override fun getItemCount(): Int = messageList.size

    class UserMessageViewHolder(var binding: AdapterRecyclerUserMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    class SelectedUserMessageViewHolder(var binding: AdapterRecyclerSelectedUserMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {}
}