package com.guiathayde.xati.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guiathayde.xati.databinding.ActivityHomeBinding
import com.guiathayde.xati.model.Chats
import com.guiathayde.xati.service.ChatConstants
import com.guiathayde.xati.service.SavedPreference
import com.guiathayde.xati.ui.adapter.ChatsAdapter
import com.guiathayde.xati.ui.viewmodel.HomeViewModel
import com.squareup.picasso.Picasso


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var savedPreference: SavedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = HomeViewModel()
        savedPreference = SavedPreference(this);

        val avatarURL = savedPreference.getAvatarURL()
        Picasso.get().load(avatarURL).into(binding.imageProfile)

        // TODO - Set to use internet chats
        binding.recyclerChats.visibility = View.GONE
        viewModel.chats.observe(this) { chats ->
            if (chats.isNotEmpty()) {
                binding.recyclerChats.layoutManager =
                    LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)

                binding.recyclerChats.adapter = ChatsAdapter { position ->
                    onListItemClick(position)
                }.apply { chatList = chats.toMutableList() }

                binding.recyclerChats.visibility = View.VISIBLE
                binding.textNoChatFound.visibility = View.GONE
                binding.textFindUsersHere.visibility = View.GONE
            }
        }

        binding.cardImageProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, SearchUserActivity::class.java))
        }
    }

    private fun onListItemClick(position: Int) {
        val selectedUser = viewModel.getSelectedUser(position)
        if (selectedUser != null) {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(ChatConstants.SELECTED_USER, selectedUser)
            startActivity(intent)
        }
    }
}