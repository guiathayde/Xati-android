package com.guiathayde.xati.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guiathayde.xati.databinding.AdapterRecyclerChatsBinding
import com.guiathayde.xati.model.Chats
import com.squareup.picasso.Picasso
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

class ChatsAdapter(private val onItemClicked: (position: Int) -> Unit) :
    RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    var chatList = mutableListOf<Chats>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            AdapterRecyclerChatsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClicked
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        chatList.forEach {
            Picasso.get().load(it.user!!.photoUrl).into(holder.binding.imageProfile)

            holder.binding.textUsername.text = it.user!!.displayName
            holder.binding.textLastMessage.text = it.lastMessage

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.timeLastMessage!!.toLong()
            val ago: String = PrettyTime(Locale.getDefault()).format(calendar)
            holder.binding.textTimeLastMessage.text = ago

            holder.binding.textChatNotification.text = it.notifications.toString()
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    class ViewHolder(
        val binding: AdapterRecyclerChatsBinding,
        private val onItemClicked: (position: Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            onItemClicked(position)
        }
    }
}