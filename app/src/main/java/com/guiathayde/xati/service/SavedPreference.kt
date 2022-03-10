package com.guiathayde.xati.service

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.guiathayde.xati.model.User

class SavedPreference(val context: Context) {

    companion object {
        private const val USER_ID = "user_id"
        private const val SELECTED_USER_ID = "selected_user_id"
        private const val EMAIL = "email"
        private const val USERNAME = "username"
        private const val AVATAR_URL = "avatar_url"
        private const val USER_CODE = "user_code"
    }

    private fun getSharedPreference(): SharedPreferences? {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun editor(const: String, string: String) {
        getSharedPreference()?.edit()?.putString(const, string)?.apply()
    }

    fun setUserId(id: String) = editor(USER_ID, id)

    fun getUserId() = getSharedPreference()?.getString(USER_ID, "")

    fun setSelectedUserId(id: String) = editor(SELECTED_USER_ID, id)

    fun getSelectedUserId() = getSharedPreference()?.getString(SELECTED_USER_ID, "")

    fun getUserEmail() = getSharedPreference()?.getString(EMAIL, "")

    fun setUserEmail(email: String) {
        editor(EMAIL, email)
    }

    fun setUserDisplayName(username: String) {
        editor(USERNAME, username)
    }

    fun getUserDisplayName() = getSharedPreference()?.getString(USERNAME, "")

    fun setUserPhotoUrl(avatarURL: String) {
        editor(AVATAR_URL, avatarURL)
    }

    fun getUserPhotoUrl() = getSharedPreference()?.getString(AVATAR_URL, "")

    fun setUserUid(userCode: String) {
        editor(USER_CODE, userCode)
    }

    fun getUserUid() = getSharedPreference()?.getString(USER_CODE, "")
}