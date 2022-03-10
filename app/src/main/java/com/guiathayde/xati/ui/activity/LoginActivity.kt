package com.guiathayde.xati.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.guiathayde.xati.databinding.ActivityLoginBinding
import com.guiathayde.xati.model.User
import com.guiathayde.xati.service.GoogleSignInClientInstance
import com.guiathayde.xati.service.SavedPreference
import java.util.*


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var savedPreference: SavedPreference
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mGoogleSignInClient = GoogleSignInClientInstance.get(this)
        firebaseAuth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        savedPreference = SavedPreference(this)

        binding.buttonSignIn.setOnClickListener { signInGoogle() }
    }

    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, REC_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REC_CODE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                updateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "Erro ao fazer login.", Toast.LENGTH_SHORT).show()
            Log.e("ERROR", e.toString())
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                savedPreference.setUserId(account.id.toString())
                savedPreference.setUserEmail(account.email.toString())
                savedPreference.setUserDisplayName(account.displayName.toString())
                savedPreference.setUserPhotoUrl(account.photoUrl.toString())

                saveNewUserOnDatabase(account.id)

                Toast.makeText(this, "Login feito com sucesso.", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Erro ao fazer login.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveNewUserOnDatabase(userId: String?) {
        database.child("users/${userId}").get().addOnSuccessListener {
            if (it.value == null) {
                val uuid = UUID.randomUUID().toString()
                    .replace("-", "")
                    .replace("[^\\d.]".toRegex(), "")
                    .substring(0, 8)

                val userData = User(
                    uuid,
                    savedPreference.getUserDisplayName(),
                    savedPreference.getUserEmail(),
                    savedPreference.getUserPhotoUrl()
                )

                val newUserId = mapOf(userId to userData)
                database.child("users").updateChildren(newUserId)
            } else {
                val userData = it.getValue(User::class.java)
                savedPreference.setUserUid(userData!!.uid.toString())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    companion object {
        private const val REC_CODE = 0
    }
}