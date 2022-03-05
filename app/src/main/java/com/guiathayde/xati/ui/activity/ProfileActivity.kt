package com.guiathayde.xati.ui.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        val userCode = savedPreference.getUserCode()
        binding.textInputUserCode.setText(userCode)
        binding.textInputUserCode.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("User Code", binding.textInputUserCode.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Código copiado.", Toast.LENGTH_SHORT).show()
        }

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