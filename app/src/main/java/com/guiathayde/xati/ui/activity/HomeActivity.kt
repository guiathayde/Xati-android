package com.guiathayde.xati.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guiathayde.xati.R
import com.guiathayde.xati.databinding.ActivityHomeBinding
import com.guiathayde.xati.model.Chats
import com.guiathayde.xati.service.SavedPreference
import com.guiathayde.xati.ui.adapter.ChatsAdapter
import com.guiathayde.xati.ui.fragment.ProfileFragment
import com.squareup.picasso.Picasso


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var savedPreference: SavedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        savedPreference = SavedPreference(this);

        val avatarURL = savedPreference.getAvatarURL()
        Picasso.get().load(avatarURL).into(binding.imageProfile)

        binding.cardImageProfile.setOnClickListener {
            // TODO - Abrir ProfileActivity
        }

        // TODO - Set to use internet chats
        val chats = mutableListOf<Chats>()
        chats.add(Chats(
            0,
            "Annette Black",
            "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80",
            "Hey, did you talk to her?",
            "1643402679373",
            2
        ))

        if (chats.size < 0) {
            binding.recyclerChats.visibility = View.GONE
        } else {
            binding.recyclerChats.adapter = ChatsAdapter(chats)
            binding.recyclerChats.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

            binding.textNoChatFound.visibility = View.GONE
            binding.textFindUsersHere.visibility = View.GONE
        }
    }
}