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

    val DataModel: MyViewModel by activityViewModels{
        MyViewModelFactory(LocalReposetoryHelper(requireContext()))
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

        binding.VariantsForEvents.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.VariantsForEvents.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.VariantsForEvents.threshold = 1
        adapter = ArrayAdapter.createFromResource(requireContext(),R.array.varinats_for_events,android.R.layout.simple_dropdown_item_1line)
        binding.VariantsForEvents.setAdapter(adapter)
        binding.VariantsForEvents.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
            binding.VariantsForEvents.setText(selectedItem)
        })

        binding.doneButton.setOnClickListener {
            Log.d("MyLog","${DataModel.userEvents.value}")
            if(!Empty()){
                val title = binding.VariantsForEvents.text.toString()
                val desc = binding.DescriptionForEvent.text.toString()
                val uniqueKey = UUID.randomUUID().toString()
                val event = Event(uniqueKey,title,desc)
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
            if(VariantsForEvents.text.isNullOrEmpty()) {
                VariantsForEvents.error = "Заполните поле"
                Log.d("MyLog", "VariantsForEvents is empty")
            }
            if(DescriptionForEvent.text.isNullOrEmpty()) {
                DescriptionForEvent.error = "Заполните поле"
                Log.d("MyLog", "DescriptionForEvent is empty")
            }
            return VariantsForEvents.text.isNullOrEmpty() || DescriptionForEvent.text.isNullOrEmpty()
        }
    }


}