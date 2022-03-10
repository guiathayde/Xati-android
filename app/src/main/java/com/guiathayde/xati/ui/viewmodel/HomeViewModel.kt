package com.guiathayde.xati.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.guiathayde.xati.model.Chats
import com.guiathayde.xati.model.Notification
import com.guiathayde.xati.model.User

class HomeViewModel : ViewModel() {

    val chats = MutableLiveData<Collection<Chats>>(
        mutableListOf(
            Chats(
                "123456",
                mutableListOf(
                    User(
                        "12345678910",
                        "Annette Black",
                        "email@email.com",
                        "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80"
                    )
                ),
                "Hey, did you talk to her?",
                1643402679373,
                listOf(
                    Notification(
                        "369173144197",
                        1
                    )
                ),
            )
        )
    )

    fun getSelectedUser(position: Int) : Chats? {
        return chats.value?.elementAt(position)
    }
}