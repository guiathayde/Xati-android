package com.guiathayde.xati.model

import java.io.Serializable

data class Message(
    val id: String? = "",
    val text: String? = "",
    val createdAt: Long? = 123456789,
    val userId: String? = ""
) : Serializable