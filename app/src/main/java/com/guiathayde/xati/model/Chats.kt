package com.guiathayde.xati.model

import java.io.Serializable

data class Chats(
    val id: Int,
    val name: String,
    val avatarURL: String?,
    val lastMessage: String?,
    val timeLastMessage: String?,
    val notifications: Int?
) : Serializable