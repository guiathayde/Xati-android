package com.guiathayde.xati.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.guiathayde.xati.model.Chats
import com.guiathayde.xati.model.Message
import com.guiathayde.xati.model.Notification
import com.guiathayde.xati.service.SavedPreference
import java.util.*


@SuppressLint("StaticFieldLeak")
class ChatViewModel(application: Application, private var chatData: Chats) :
    AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    private var savedPreference: SavedPreference = SavedPreference(context)
    private var database: DatabaseReference = Firebase.database.reference

    fun sendMessage(textMessage: String) {
        val newMessage = Message(
            UUID.randomUUID().toString()
                .replace("-", "")
                .replace("[^\\d.]".toRegex(), "")
                .substring(0, 6),
            textMessage,
            Calendar.getInstance().timeInMillis,
            savedPreference.getUserId()
        )

        val toUser = chatData.users!!.filter { it.email != savedPreference.getUserEmail() }[0]

        val notification = mutableListOf<Notification>()
        if (chatData.notifications == null) {
            notification.add(
                Notification(
                    toUser.uid,
                    1
                )
            )
            notification.add(
                Notification(
                    savedPreference.getUserUid(),
                    0
                )
            )

            database.child("users").child(savedPreference.getUserId()!!).child("chatsIds").push()
                .setValue(chatData.id)
            database.child("users").child(savedPreference.getSelectedUserId()!!).child("chatsIds")
                .push().setValue(chatData.id)
        } else {
            val toUserNotification = chatData.notifications!!.find { it!!.uid == toUser.uid }
            val currentUserNotification = chatData.notifications!!.find { it!!.uid == savedPreference.getUserUid() }

            toUserNotification!!.totalNewMessages = toUserNotification.totalNewMessages!!.plus(1)
            currentUserNotification!!.totalNewMessages = 0

            notification.add(toUserNotification)
            notification.add(currentUserNotification)
        }

        val newChatData = Chats(
            chatData.id,
            chatData.users,
            textMessage,
            Calendar.getInstance().timeInMillis,
            notification
        )

        database.child("chats").child(chatData.id!!).setValue(newChatData)
        database.child("messages").child(chatData.id!!).push().setValue(newMessage)
    }
}