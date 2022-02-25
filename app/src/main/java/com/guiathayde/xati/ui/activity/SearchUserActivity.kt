package com.guiathayde.xati.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.guiathayde.xati.databinding.ActivitySearchUserBinding
import com.guiathayde.xati.service.SavedPreference

class SearchUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchUserBinding
    private lateinit var savedPreference: SavedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        savedPreference = SavedPreference(this)

        binding.layoutUserFound.visibility = View.GONE
        binding.textUserNotFound.visibility = View.GONE
        binding.imageUserNotFound.visibility = View.GONE

        binding.buttonBack.setOnClickListener { onBackPressed() }
        binding.buttonSearch.setOnClickListener {
            val usercode = binding.textInputUserCode.text.toString()
            // TODO - Search for code user in Firebase database
        }
    }
}