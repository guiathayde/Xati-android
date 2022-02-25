package com.guiathayde.xati.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.guiathayde.xati.R
import com.guiathayde.xati.databinding.FragmentProfileBinding
import com.guiathayde.xati.service.SavedPreference
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var savedPreference: SavedPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedPreference = SavedPreference(requireContext())

        val avatarURL = savedPreference.getAvatarURL()
        Picasso.get().load(avatarURL).into(binding.imageProfile)

        val username = savedPreference.getUsername()
        binding.textInputName.setText(username)
    }
}