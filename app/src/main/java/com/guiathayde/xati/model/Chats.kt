package com.guiathayde.xati.model

import java.util.*

data class Chats(
    val id: Int,
    val name: String,
    val avatarURL: String?,
    val lastMessage: String?,
    val timeLastMessage: String?,
    val notifications: Int?
)