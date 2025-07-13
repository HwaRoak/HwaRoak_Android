package com.example.hwaroak.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hwaroak.R
import com.example.hwaroak.databinding.FragmentEditProfileBinding
import com.example.hwaroak.databinding.FragmentMypageBinding

class EditProfileFragment : Fragment() {

    lateinit var binding: FragmentEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        binding.btnSave.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }
}