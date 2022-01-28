package com.guiathayde.xati

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.guiathayde.xati.databinding.FragmentUploadProfilePhotoBinding

class UploadProfilePhotoFragment : Fragment() {

    private lateinit var binding: FragmentUploadProfilePhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUploadProfilePhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonEditPhoto.setOnClickListener {
            ModalBottomDialogFragment().show(activity!!.supportFragmentManager, "modalBottomDialogCamera" )
        }
    }
}