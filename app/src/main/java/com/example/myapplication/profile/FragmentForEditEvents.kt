package com.example.myapplication.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.fragment.app.activityViewModels
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentForEditEventsBinding
import com.example.myapplication.elements.Event
import com.example.myapplication.reposetory.LocalReposetory
import com.example.myapplication.reposetory.LocalReposetoryHelper
import com.example.myapplication.viewmodel.MyViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.UUID

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentForEditEvents.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentForEditEvents : BottomSheetDialogFragment() {
    var selectedValue = ""

    val DataModel: MyViewModel by activityViewModels{
        MyViewModelFactory(LocalReposetoryHelper(requireContext()),requireActivity().application)
    }
    private lateinit var binding: FragmentForEditEventsBinding
    private lateinit var adapter: ArrayAdapter<CharSequence>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentForEditEventsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {



        adapter = ArrayAdapter.createFromResource(requireContext(),R.array.varinats_for_events,android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.VariantsForEvents.setAdapter(adapter)

        binding.VariantsForEvents.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedValue = parent?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        binding.doneButton.setOnClickListener {
            Log.d("MyLog","${DataModel.userEvents.value}")
            if(!Empty()){

                val desc = binding.DescriptionForEvent.text.toString()
                val uniqueKey = UUID.randomUUID().toString()
                val event = Event(uniqueKey,selectedValue,desc)
                DataModel.addEventToReposetory(event,1)

                dismiss()
            }

        }


    }

    companion object {

        @JvmStatic
        fun newInstance() = FragmentForEditEvents()
    }
    fun Empty() : Boolean {
        binding.apply {

            if(DescriptionForEvent.text.isNullOrEmpty()) {
                DescriptionForEvent.error = "Заполните поле"
                Log.d("MyLog", "DescriptionForEvent is empty")
            }
            return  DescriptionForEvent.text.isNullOrEmpty()
        }
    }


}