package com.guiathayde.xati.model

import java.io.Serializable

data class Notification(
    var uid: String?,
    var totalNewMessages: Int? = 0
) : Serializable