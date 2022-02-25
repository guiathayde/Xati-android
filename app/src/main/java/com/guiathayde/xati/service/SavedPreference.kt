package com.guiathayde.xati.service

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SavedPreference(val context: Context) {

    companion object {
        private const val EMAIL = "email"
        private const val USERNAME = "username"
        private const val AVATAR_URL = "avatar_url"
    }

    private fun getSharedPreference(): SharedPreferences? {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun editor(const: String, string: String) {
        getSharedPreference()?.edit()?.putString(const, string)?.apply()
    }

    fun getEmail() = getSharedPreference()?.getString(EMAIL, "")

    fun setEmail(email: String) {
        editor(EMAIL, email)
    }

    fun setUsername(username: String) {
        editor(USERNAME, username)
    }

    fun getUsername() = getSharedPreference()?.getString(USERNAME, "")

    fun setAvatarURL(avatarURL: String) {
        editor(AVATAR_URL, avatarURL)
    }

    fun getAvatarURL() = getSharedPreference()?.getString(AVATAR_URL, "")
}