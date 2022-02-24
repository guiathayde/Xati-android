package com.guiathayde.xati.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.guiathayde.xati.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.buttonSignUp.setOnClickListener {
//            val uuid = UUID.randomUUID().toString()
//                .replace("-", "")
//                .replace("[^\\d.]".toRegex(), "")
//                .substring(0, 12)
//            Log.i("UUID", uuid)

             startActivity(Intent(this, HomeActivity::class.java))
        }
    }
}