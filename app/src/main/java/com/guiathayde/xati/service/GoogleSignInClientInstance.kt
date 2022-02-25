package com.guiathayde.xati.service

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.guiathayde.xati.R

object GoogleSignInClientInstance {
    fun get(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.request_id_token_google_sign_in))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }
}