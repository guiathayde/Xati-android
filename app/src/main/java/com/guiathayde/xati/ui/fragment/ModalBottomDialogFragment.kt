package com.guiathayde.xati.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.guiathayde.xati.R
import com.guiathayde.xati.databinding.FragmentModalBottomDialogBinding

class ModalBottomDialogFragment(private val cameraCallback: () -> Unit, val galleryCallback: () -> Unit) :
    BottomSheetDialogFragment() {

    private lateinit var binding: FragmentModalBottomDialogBinding

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentModalBottomDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCamera.setOnClickListener {
            cameraCallback()
            dismiss()
        }
        binding.buttonGallery.setOnClickListener {
            galleryCallback()
            dismiss()
        }
    }
}