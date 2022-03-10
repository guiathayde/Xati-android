package com.guiathayde.xati.model

import com.guiathayde.xati.model.Notification
import java.io.Serializable

class Chats(
    var id: String? = null,
    var users: Collection<User>? = null,
    var lastMessage: String? = null,
    var timeLastMessage: Long? = null,
    var notifications: Collection<Notification?>? = null
) : Serializable