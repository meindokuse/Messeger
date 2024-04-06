package com.example.myapplication.ui.profile.editable

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
import com.example.myapplication.ui.profile.viewmodel.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID




@AndroidEntryPoint
class FragmentForEditEvents : BottomSheetDialogFragment() {

    private val descriptionVoiceFragment = DescriptionVoiceFragment()
    private val descriptionTextFragment = DescriptionTextFragment()

    var TextOrVoice = 1


    private val DataModel: ProfileViewModel by activityViewModels()
    private lateinit var binding: FragmentForEditEventsBinding
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