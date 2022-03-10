package com.guiathayde.xati.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.guiathayde.xati.databinding.ActivityChatBinding
import com.guiathayde.xati.model.Chats
import com.guiathayde.xati.model.Message
import com.guiathayde.xati.service.ChatConstants
import com.guiathayde.xati.service.SavedPreference
import com.guiathayde.xati.ui.adapter.MessagesAdapter
import com.guiathayde.xati.ui.viewmodel.ChatViewModel
import com.squareup.picasso.Picasso

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var savedPreference: SavedPreference
    private lateinit var database: DatabaseReference
    private var messageList = MutableLiveData<Collection<Message>>()
    private var messageListUpdated = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val chatData = intent.getSerializableExtra(ChatConstants.SELECTED_USER) as Chats

        viewModel = ChatViewModel(this.application, chatData)
        savedPreference = SavedPreference(this)
        database = Firebase.database.reference

        val user = chatData.users!!.filter { user ->
            user.email != savedPreference.getUserEmail()
        }

        binding.textUsername.text = user[0].displayName
        if (user[0].photoUrl!!.isNotEmpty()) {
            Picasso.get().load(user[0].photoUrl).into(binding.imageProfile)
        }

        database.child("messages").child(chatData.id!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageListUpdated.clear()

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageListUpdated.add(message!!)
                    }
                    messageList.value = messageListUpdated
                }

                override fun onCancelled(error: DatabaseError) {}

            })

        messageList.observe(this) { messages ->
            if (messages.isNotEmpty()) {
                binding.recyclerMessages.layoutManager =
                    LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)

                binding.recyclerMessages.adapter =
                    MessagesAdapter(this).apply { messageList = messages.toMutableList() }
            }
        }

        binding.buttonBack.setOnClickListener { onBackPressed() }
        binding.buttonSendMessage.setOnClickListener {
            viewModel.sendMessage(binding.textInputMessage.text.toString())
        }
    }
}