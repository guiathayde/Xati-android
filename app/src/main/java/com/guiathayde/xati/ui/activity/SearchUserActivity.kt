package com.guiathayde.xati.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.guiathayde.xati.databinding.ActivitySearchUserBinding
import com.guiathayde.xati.model.Chats
import com.guiathayde.xati.model.User
import com.guiathayde.xati.service.ChatConstants
import com.guiathayde.xati.service.SavedPreference
import com.squareup.picasso.Picasso

class SearchUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchUserBinding
    private lateinit var savedPreference: SavedPreference
    private lateinit var database: DatabaseReference
    private lateinit var selectedUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        savedPreference = SavedPreference(this)

        database = Firebase.database.reference

        binding.layoutUserFound.visibility = View.GONE
        binding.textUserNotFound.visibility = View.GONE
        binding.imageUserNotFound.visibility = View.GONE

        binding.buttonBack.setOnClickListener { onBackPressed() }
        binding.buttonSearch.setOnClickListener {
            val usercode = binding.textInputUserCode.text.toString()
            database.child("users").orderByChild("uid").equalTo(usercode).get().addOnSuccessListener {
                if (it.exists()) {
                    selectedUser = User()
                    it.children.forEach { userData ->
                        userData.children.forEach { eachUserData ->
                            when (eachUserData.key.toString()) {
                                "displayName" -> selectedUser.displayName = eachUserData.value.toString()
                                "email" -> selectedUser.email = eachUserData.value.toString()
                                "photoUrl" -> selectedUser.photoUrl = eachUserData.value.toString()
                                "uid" -> selectedUser.uid = eachUserData.value.toString()
                            }
                        }
                    }

                    Picasso.get().load(selectedUser.photoUrl).into(binding.imageProfile)
                    binding.textUsername.text = selectedUser.displayName

                    binding.layoutUserFound.visibility = View.VISIBLE
                } else {
                    binding.textUserNotFound.visibility = View.VISIBLE
                    binding.imageUserNotFound.visibility = View.VISIBLE
                }
            }
        }
        binding.layoutUserFound.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(ChatConstants.SELECTED_USER, Chats(selectedUser))
            startActivity(intent)
        }
    }
}