package com.guiathayde.xati.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.guiathayde.xati.R
import com.guiathayde.xati.databinding.ActivityChatBinding
import com.guiathayde.xati.model.Chats
import com.guiathayde.xati.service.ChatConstants
import com.guiathayde.xati.service.SavedPreference
import com.squareup.picasso.Picasso

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var savedPreference: SavedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        savedPreference = SavedPreference(this)

        val selectedUser = intent.getSerializableExtra(ChatConstants.SELECTED_USER) as Chats

        binding.textUsername.text = selectedUser.name
        Picasso.get().load(selectedUser.avatarURL).into(binding.imageProfile)

        binding.buttonBack.setOnClickListener { onBackPressed() }
    }
}