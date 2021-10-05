package com.example.runningapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.runningapp.R
import com.example.runningapp.databinding.FragmentSetupBinding

class FragmentSetup : Fragment() {

    private lateinit var binding:FragmentSetupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentSetupBinding.inflate(inflater,container,false)

        binding.tvContinue.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentSetup_to_fragmentRun)
        }

        return binding.root
    }

}