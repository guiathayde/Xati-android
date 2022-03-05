package com.guiathayde.xati.model

import java.io.Serializable

class Chats(
    var user: User?,
    var lastMessage: String? = null,
    var timeLastMessage: String? = null,
    var notifications: Int? = null
) : Serializable