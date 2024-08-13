package com.example.friendnet.ui.profile.editable

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.friendnet.databinding.FragmentDescriptionTextBinding

class DescriptionTextFragment : Fragment() {
    private lateinit var binding:FragmentDescriptionTextBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDescriptionTextBinding.inflate(inflater)
        return binding.root
    }

    fun getData():String?{
        if (binding.DescriptionForEvent.text.isNullOrEmpty()) return null
       return binding.DescriptionForEvent.text.toString()
    }
    fun error(){
        binding.DescriptionForEvent.error = "Заполните поле"
    }
}