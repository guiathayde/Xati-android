package com.guiathayde.xati

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guiathayde.xati.databinding.AdapterRecyclerChatsBinding
import com.guiathayde.xati.model.Chats
import com.squareup.picasso.Picasso
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

class ChatsAdapter(private val chats: Collection<Chats>) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    class ViewHolder(val binding: AdapterRecyclerChatsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            AdapterRecyclerChatsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        chats.forEach {
            Picasso.get().load(it.avatarURL).into(holder.binding.imageProfile)

            holder.binding.textUsername.text = it.name
            holder.binding.textLastMessage.text = it.lastMessage

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.timeLastMessage!!.toLong()
            val ago: String = PrettyTime(Locale.getDefault()).format(calendar)
            holder.binding.textTimeLastMessage.text = ago

            holder.binding.textChatNotification.text = it.notifications.toString()
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }
}