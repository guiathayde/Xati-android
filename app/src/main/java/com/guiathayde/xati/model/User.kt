package com.guiathayde.xati.model

import java.io.Serializable

data class User(
    var uid: String? = null,
    var displayName: String? = null,
    var email: String? = null,
    var photoUrl: String? = null,
) : Serializable