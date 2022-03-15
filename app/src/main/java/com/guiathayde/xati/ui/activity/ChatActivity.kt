package com.guiathayde.xati.ui.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.guiathayde.xati.databinding.ActivityChatBinding
import com.guiathayde.xati.model.Chats
import com.guiathayde.xati.model.Message
import com.guiathayde.xati.model.Notification
import com.guiathayde.xati.service.ChatConstants
import com.guiathayde.xati.service.SavedPreference
import com.guiathayde.xati.ui.adapter.MessagesAdapter
import com.guiathayde.xati.ui.viewmodel.ChatViewModel

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var savedPreference: SavedPreference
    private lateinit var database: DatabaseReference
    private lateinit var chatData: Chats
    private var messageList = MutableLiveData<Collection<Message>>()
    private var messageListUpdated = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        chatData = intent.getSerializableExtra(ChatConstants.SELECTED_USER) as Chats

        viewModel = ChatViewModel(this.application, chatData)
        savedPreference = SavedPreference(this)
        database = Firebase.database.reference

        val user = chatData.users!!.filter { user ->
            user.email != savedPreference.getUserEmail()
        }

        binding.textUsername.text = user[0].displayName
        if (user[0].photoUrl!!.isNotEmpty()) {
            Glide.with(this).load(user[0].photoUrl).into(binding.imageProfile)
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
                val recyclerLayoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
                recyclerLayoutManager.stackFromEnd = true
                binding.recyclerMessages.layoutManager = recyclerLayoutManager

                binding.recyclerMessages.adapter =
                    MessagesAdapter(this).apply { messageList = messages.toMutableList() }
            }
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                cleanNotifications()
                finish()
            }
        })
        binding.buttonBack.setOnClickListener {
            cleanNotifications()
            finish()
        }
        binding.buttonSendMessage.setOnClickListener {
            viewModel.sendMessage(binding.textInputMessage.text.toString())
            binding.textInputMessage.text!!.clear()
        }
    }

    private fun cleanNotifications() {
        val notificationsUpdated = mutableListOf<Notification>()
        database.child("chats").child(chatData.id!!).child("notifications").get().addOnSuccessListener {
            notificationsUpdated.clear()
            it.children.forEach { notificationSnapshot ->
                val notification = notificationSnapshot.getValue(Notification::class.java)
                if (notification!!.uid == savedPreference.getUserUid()) {
                    val currentUserNotification = Notification(notification.uid, 0)
                    notificationsUpdated.add(currentUserNotification)
                } else {
                    notificationsUpdated.add(notification)
                }
            }
            database.child("chats").child(chatData.id!!).child("notifications")
                .setValue(notificationsUpdated)
        }
    }
}