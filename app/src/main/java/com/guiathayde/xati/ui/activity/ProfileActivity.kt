package com.guiathayde.xati.ui.activity

import android.Manifest
import android.R.attr.bitmap
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.guiathayde.xati.databinding.ActivityProfileBinding
import com.guiathayde.xati.service.GoogleSignInClientInstance
import com.guiathayde.xati.service.SavedPreference
import com.guiathayde.xati.ui.fragment.ModalBottomDialogFragment
import java.io.File


class ProfileActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 101
        private const val REQUEST_CODE_CAMERA = 0
        private const val REQUEST_CODE_GALLERY = 1
    }

    private lateinit var binding: ActivityProfileBinding
    private lateinit var savedPreference: SavedPreference
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var storage: StorageReference
    private lateinit var database: DatabaseReference
    private lateinit var photoFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        savedPreference = SavedPreference(this)

        mGoogleSignInClient = GoogleSignInClientInstance.get(this)
        storage = Firebase.storage.reference
        database = Firebase.database.reference

        val avatarURL = savedPreference.getUserPhotoUrl()
        Glide.with(this).load(avatarURL).into(binding.imageProfile);

        val username = savedPreference.getUserDisplayName()
        binding.textInputName.setText(username)

        val userCode = savedPreference.getUserUid()
        binding.textInputUserCode.setText(userCode)
        binding.textInputUserCode.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("User Code", binding.textInputUserCode.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Código copiado.", Toast.LENGTH_SHORT).show()
        }

        binding.buttonEditPhoto.setOnClickListener {
            if (checkAndRequestPermissions()) {
                ModalBottomDialogFragment(::onCameraPhoto, ::onGalleryPhoto).show(
                    supportFragmentManager,
                    "modal_camera_gallery"
                )
            }
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

    private fun onCameraPhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile()

        val fileProvider =
            FileProvider.getUriForFile(this, "com.guiathayde.xati.fileprovider", photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
    }

    private fun onGalleryPhoto() {
        val pickPhotoIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhotoIntent, REQUEST_CODE_GALLERY)
    }

    private fun getPhotoFile(): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("photo.jpg", ".jpg", storageDirectory)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(
                        applicationContext,
                        "É necessário permitir acesso a camera.", Toast.LENGTH_SHORT
                    )
                        .show()
                }
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(
                        applicationContext,
                        "É necessário permitir acesso ao armazenamento.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    ModalBottomDialogFragment(::onCameraPhoto, ::onGalleryPhoto).show(
                        supportFragmentManager,
                        "modal_camera_gallery"
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            updatePhotoProfile(Uri.fromFile(photoFile))
        } else if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage: Uri = data.data!!
            updatePhotoProfile(selectedImage)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun updatePhotoProfile(uriPhoto: Uri) {
        binding.imageProfile.visibility = View.GONE
        val ref = storage.child("users/" + savedPreference.getUserId())
        ref.putFile(uriPhoto)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    val imageUrl = it.toString()

                    val profileUpdates = userProfileChangeRequest {
                        photoUri = Uri.parse(imageUrl)
                    }
                    Firebase.auth.currentUser!!.updateProfile(profileUpdates).addOnSuccessListener {
                        database.child("users").child(savedPreference.getUserId()!!)
                            .child("photoUrl").setValue(imageUrl)

                        savedPreference.setUserPhotoUrl(imageUrl)

                        Toast.makeText(this, "Image Uploaded!", Toast.LENGTH_SHORT).show()

                        Glide.with(this).load(imageUrl).into(binding.imageProfile);
                        binding.imageProfile.visibility = View.VISIBLE
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val storePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val cameraPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (storePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                .add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this, listPermissionsNeeded
                    .toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }
}