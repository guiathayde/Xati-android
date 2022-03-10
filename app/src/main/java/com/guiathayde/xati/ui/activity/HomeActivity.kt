package com.guiathayde.xati.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.guiathayde.xati.databinding.ActivityHomeBinding
import com.guiathayde.xati.model.Chats
import com.guiathayde.xati.model.Message
import com.guiathayde.xati.model.Notification
import com.guiathayde.xati.model.User
import com.guiathayde.xati.service.ChatConstants
import com.guiathayde.xati.service.SavedPreference
import com.guiathayde.xati.ui.adapter.ChatsAdapter
import com.guiathayde.xati.ui.viewmodel.HomeViewModel
import com.squareup.picasso.Picasso


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var savedPreference: SavedPreference
    private lateinit var database: DatabaseReference
    private var chatList = MutableLiveData<Collection<Chats>>()
    private val chatListUpdated = mutableListOf<Chats>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = HomeViewModel()
        savedPreference = SavedPreference(this)
        database = Firebase.database.reference

        val avatarURL = savedPreference.getUserPhotoUrl()
        Picasso.get().load(avatarURL).into(binding.imageProfile)

        binding.recyclerChats.visibility = View.GONE

        val userId = savedPreference.getUserId() ?: ""
        database.child("users").child(userId).child("chatsIds")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (postSnapshot in snapshot.children) {
                        chatListUpdated.clear()
                        val chatId = postSnapshot.value.toString()
                        database.child("chats").child(chatId)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(chat: DataSnapshot) {
                                    if (chat.exists()) {
                                        val chatUsers = mutableListOf<User>()
                                        chat.child("users").children.forEach { user ->
                                            val addUser = User(
                                                uid = user.child("uid").value.toString(),
                                                displayName = user.child("displayName").value.toString(),
                                                email = user.child("email").value.toString(),
                                                photoUrl = user.child("photoUrl").value.toString(),
                                            )
                                            chatUsers.add(addUser)
                                        }

                                        val chatNotifications = mutableListOf<Notification>()
                                        chat.child("notifications").children.forEach { notification ->
                                            val addNotification = Notification(
                                                uid = notification.child("uid").value.toString(),
                                                totalNewMessages = notification.child("totalNewMessages").value.toString()
                                                    .toInt()
                                            )
                                            chatNotifications.add(addNotification)
                                        }

                                        val chatLastMessage =
                                            chat.child("lastMessage").value.toString()
                                        val chatTimeLastMessage =
                                            chat.child("timeLastMessage").value.toString().toLong()
                                        val newChat = Chats(
                                            chatId,
                                            chatUsers,
                                            chatLastMessage,
                                            chatTimeLastMessage,
                                            chatNotifications
                                        )

                                        val oldChatPosition =
                                            chatListUpdated.indexOfFirst { it.id == newChat.id }
                                        if (oldChatPosition >= 0) {
                                            chatListUpdated[oldChatPosition] = newChat
                                        } else {
                                            chatListUpdated.add(newChat)
                                        }
                                        chatList.value = chatListUpdated
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })

        chatList.observe(this) { chats ->
            if (chats.isNotEmpty()) {
                binding.recyclerChats.layoutManager =
                    LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)

                binding.recyclerChats.adapter = ChatsAdapter { position ->
                    onListItemClick(position)
                }.apply { chatList = chats.toMutableList() }

                binding.recyclerChats.visibility = View.VISIBLE
                binding.textNoChatFound.visibility = View.GONE
                binding.textFindUsersHere.visibility = View.GONE
            }
        }

        binding.cardImageProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, SearchUserActivity::class.java))
        }
    }

    private fun onListItemClick(position: Int) {
        val selectedUser = chatList.value!!.elementAt(position)
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(ChatConstants.SELECTED_USER, selectedUser)
        startActivity(intent)
    }
}