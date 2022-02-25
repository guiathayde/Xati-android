package com.guiathayde.xati.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.guiathayde.xati.databinding.ActivityProfileBinding
import com.guiathayde.xati.service.GoogleSignInClientInstance
import com.guiathayde.xati.service.SavedPreference
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var savedPreference: SavedPreference
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        savedPreference = SavedPreference(this)

        mGoogleSignInClient = GoogleSignInClientInstance.get(this)

        val avatarURL = savedPreference.getAvatarURL()
        Picasso.get().load(avatarURL).into(binding.imageProfile)

        val username = savedPreference.getUsername()
        binding.textInputName.setText(username)

        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }

        binding.buttonLogout.setOnClickListener {
            mGoogleSignInClient.signOut().addOnCompleteListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}