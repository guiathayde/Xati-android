package com.guiathayde.xati.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.guiathayde.xati.databinding.ActivitySearchUserBinding
import com.guiathayde.xati.model.Chats
import com.guiathayde.xati.model.Notification
import com.guiathayde.xati.model.User
import com.guiathayde.xati.model.UserChatData
import com.guiathayde.xati.service.ChatConstants
import com.guiathayde.xati.service.SavedPreference
import com.squareup.picasso.Picasso
import java.util.*

class SearchUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchUserBinding
    private lateinit var savedPreference: SavedPreference
    private lateinit var database: DatabaseReference
    private lateinit var selectedUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        savedPreference = SavedPreference(this)

        database = Firebase.database.reference

        binding.layoutUserFound.visibility = View.GONE
        binding.textUserNotFound.visibility = View.GONE
        binding.imageUserNotFound.visibility = View.GONE

        binding.buttonBack.setOnClickListener { onBackPressed() }
        binding.buttonSearch.setOnClickListener {
            val usercode = binding.textInputUserCode.text.toString()
            if (usercode != savedPreference.getUserUid()) {
                database.child("users").orderByChild("uid").equalTo(usercode).get()
                    .addOnSuccessListener {
                        this.currentFocus?.let { view ->
                            val imm =
                                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.hideSoftInputFromWindow(view.windowToken, 0)
                        }

                        if (it.exists()) {
                            selectedUser = User()
                            it.children.forEach { userData ->
                                savedPreference.setSelectedUserId(userData.key.toString())
                                userData.children.forEach { eachUserData ->
                                    when (eachUserData.key.toString()) {
                                        "displayName" -> selectedUser.displayName =
                                            eachUserData.value.toString()
                                        "email" -> selectedUser.email =
                                            eachUserData.value.toString()
                                        "photoUrl" -> selectedUser.photoUrl =
                                            eachUserData.value.toString()
                                        "uid" -> selectedUser.uid = eachUserData.value.toString()
                                    }
                                }
                            }

                            Picasso.get().load(selectedUser.photoUrl).into(binding.imageProfile)
                            binding.textUsername.text = selectedUser.displayName

                            binding.layoutUserFound.visibility = View.VISIBLE
                        } else {
                            binding.textUserNotFound.visibility = View.VISIBLE
                            binding.imageUserNotFound.visibility = View.VISIBLE
                        }
                    }
            } else {
                Toast.makeText(
                    this,
                    "Não é possível criar uma conversa com você mesmo.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.layoutUserFound.setOnClickListener {
            val currentUserId = savedPreference.getUserId()
            database.child("users").child(currentUserId!!)
                .get().addOnSuccessListener { userData ->
                    val chatAlreadyExists = UserChatData()
                    userData.child("userChatsData").children.forEach { userChatData ->
                        val userChatData = userChatData.getValue(UserChatData::class.java)
                        if (userChatData!!.withUser == selectedUser.uid) {
                            chatAlreadyExists.chatId = userChatData.chatId
                            chatAlreadyExists.withUser = userChatData.withUser
                        }
                    }

                    if (chatAlreadyExists.chatId == null) {
                        val newChatId = UUID.randomUUID().toString()
                            .replace("-", "")
                            .replace("[^\\d.]".toRegex(), "")
                            .substring(0, 6)

                        val currentUser = User(
                            uid = savedPreference.getUserUid(),
                            displayName = savedPreference.getUserDisplayName(),
                            email = savedPreference.getUserEmail(),
                            photoUrl = savedPreference.getUserPhotoUrl(),
                        )

                        val intent = Intent(this, ChatActivity::class.java)
                        intent.putExtra(
                            ChatConstants.SELECTED_USER,
                            Chats(
                                id = newChatId,
                                users = mutableListOf(currentUser, selectedUser)
                            )
                        )
                        startActivity(intent)
                    } else {
                        database.child("chats").child(chatAlreadyExists.chatId!!).get()
                            .addOnSuccessListener { chatData ->
                                val chatId = chatData.child("id").value.toString()

                                val chatUsers = mutableListOf<User>()
                                chatData.child("users").children.forEach { user ->
                                    val addUser = User(
                                        uid = user.child("uid").value.toString(),
                                        displayName = user.child("displayName").value.toString(),
                                        email = user.child("email").value.toString(),
                                        photoUrl = user.child("photoUrl").value.toString(),
                                    )
                                    chatUsers.add(addUser)
                                }

                                val chatNotifications = mutableListOf<Notification>()
                                chatData.child("notifications").children.forEach { notification ->
                                    val addNotification = Notification(
                                        uid = notification.child("uid").value.toString(),
                                        totalNewMessages = notification.child("totalNewMessages").value.toString()
                                            .toInt()
                                    )
                                    chatNotifications.add(addNotification)
                                }

                                val chatLastMessage =
                                    chatData.child("lastMessage").value.toString()
                                val chatTimeLastMessage =
                                    chatData.child("timeLastMessage").value.toString().toLong()
                                val chat = Chats(
                                    chatId,
                                    chatUsers,
                                    chatLastMessage,
                                    chatTimeLastMessage,
                                    chatNotifications
                                )

                                val intent = Intent(this, ChatActivity::class.java)
                                intent.putExtra(
                                    ChatConstants.SELECTED_USER,
                                    chat
                                )
                                startActivity(intent)
                            }
                    }
                }
        }
    }
}