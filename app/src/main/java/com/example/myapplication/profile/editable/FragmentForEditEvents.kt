package com.example.myapplication.profile.editable

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentForEditEventsBinding
import com.example.myapplication.models.Event
import com.example.myapplication.profile.domain.MyViewModelFactory
import com.example.myapplication.reposetory.LocalReposetoryHelper
import com.example.myapplication.profile.domain.MyViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import java.util.UUID

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentForEditEvents.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentForEditEvents : BottomSheetDialogFragment() {

    val descriptionVoiceFragment = DescriptionVoiceFragment()
    val descriptionTextFragment = DescriptionTextFragment()

    var TextOrVoice = 1


    val DataModel: MyViewModel by activityViewModels{
        MyViewModelFactory(LocalReposetoryHelper(requireContext()),requireActivity().application)
    }
    private lateinit var binding: FragmentForEditEventsBinding
    private lateinit var adapter: ArrayAdapter<CharSequence>
    private var isCompleteAudio = false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentForEditEventsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        showTextDescriptionEditText()

        DataModel.isRecording.observe(viewLifecycleOwner){
            binding.doneButton.isEnabled = !it
        }
        isCompleteAudio = false

        binding.tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 -> {
                        showTextDescriptionEditText()
                        TextOrVoice = 1
                    }
                    1 -> {
                        showVoiceRecordingInterface()
                        TextOrVoice = 2
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        binding.doneButton.setOnClickListener {
            Log.d("MyLog","${DataModel.userEvents.value}")
            val title = binding.titileForEvent.text.toString()

            if (TextOrVoice == 1 && !Empty()){
                val desc = descriptionTextFragment.getData()
                if (desc == null){
                    descriptionTextFragment.error()
                }else {
                    val uniqueKey = UUID.randomUUID().toString()
                    val currentTime = System.currentTimeMillis()
                    val event = Event(uniqueKey, title, desc,currentTime,TextOrVoice)
                    DataModel.addEventToReposetory(event)
                    dismiss()
                }
                }
            if (TextOrVoice == 2 ){
                val audioDesc = descriptionVoiceFragment.getAudio()
                if (audioDesc == null){
                    Toast.makeText(requireContext(),"Произошла ошибка записи",Toast.LENGTH_SHORT).show()
                }else{
                    isCompleteAudio = true
                    val uniqueKey = UUID.randomUUID().toString()
                    val currentTime = System.currentTimeMillis()
                    val event = Event(uniqueKey, title, audioDesc,currentTime,TextOrVoice)
                    DataModel.addEventToReposetory(event)
                    dismiss()
                }
                }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (!isCompleteAudio && descriptionVoiceFragment.getAudio() != null) {
            descriptionVoiceFragment.clearAudioPath()
        }
        if (isCompleteAudio ) descriptionVoiceFragment.audioDescToNull()
        super.onDismiss(dialog)
    }

    companion object {

        @JvmStatic
        fun newInstance() = FragmentForEditEvents()
    }
    fun Empty() : Boolean {
        binding.apply {

            if(descriptionTextFragment.getData().isNullOrEmpty()) {
                descriptionTextFragment.error()
                Log.d("MyLog", "DescriptionForEvent is empty")
            }
            else if (binding.titileForEvent.text.isNullOrEmpty()){
                binding.titileForEvent.error = "Заполните поле"
            }
            return  descriptionTextFragment.getData().isNullOrEmpty() || binding.titileForEvent.text.isNullOrEmpty()
        }
    }
    private fun showVoiceRecordingInterface(){
        childFragmentManager.beginTransaction()
            .replace(R.id.descriptionContainer,descriptionVoiceFragment)
            .commit()
    }
    private fun showTextDescriptionEditText(){
        childFragmentManager.beginTransaction()
            .replace(R.id.descriptionContainer,descriptionTextFragment)
            .commit()
    }
}