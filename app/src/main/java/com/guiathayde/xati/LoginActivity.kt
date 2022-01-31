package com.guiathayde.xati

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.guiathayde.xati.databinding.ActivityLoginBinding
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.buttonSignUp.setOnClickListener {
            val uuid = UUID.randomUUID().toString()
                .replace("-", "")
                .replace("[^\\d.]".toRegex(), "")
                .substring(0, 12)
            Log.i("UUID", uuid)

            // startActivity(Intent(this, HomeActivity::class.java))
        }
    }
}