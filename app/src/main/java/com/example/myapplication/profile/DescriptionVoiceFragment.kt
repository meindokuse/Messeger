package com.example.myapplication.profile

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.myapplication.reposetory.LocalReposetoryHelper
import com.example.myapplication.viewmodel.MyViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.Manifest
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.coroutineContext


class DescriptionVoiceFragment : Fragment() {



    val profileViewModel: MyViewModel by activityViewModels{
        MyViewModelFactory(LocalReposetoryHelper(requireContext()), requireActivity().application)
    }
    private var audioDesc:String? = null
    var time = 0
    lateinit var textProcess:TextView
    private var timerJob: Job? = null

    private lateinit var startOrStopButton:FloatingActionButton
    private lateinit var stopButton:FloatingActionButton
    private lateinit var cancelButton: FloatingActionButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_description_voice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startOrStopButton = view.findViewById(R.id.startRecordingButton)
        stopButton = view.findViewById(R.id.stopButton)
        textProcess = view.findViewById(R.id.durationText)
        cancelButton = view.findViewById(R.id.cancelButton)
        time = 0

        stopButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
        textProcess.text = formatDuration(0)

        startOrStopButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // Если нет, то запрашиваем разрешение
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_REQUEST_CODE)
            } else {
                // Если разрешение уже есть, выполняем запись
                stopButton.isEnabled = true
                clearAudioPath()
                audioDesc = profileViewModel.startRecording()
                profileViewModel.isRecording.value = true
                startRecordingTimer()
                setStartButtonToStop(stopButton,startOrStopButton)

            }
        }
        stopButton.setOnClickListener {
            profileViewModel.stopRecording()
            profileViewModel.isRecording.value = false
            stopButton.isEnabled = false
            stopRecordingTimer()
            startOrStopButton.setImageResource(R.drawable.baseline_settings_backup_restore_24)
            setStartButtonToStop(startOrStopButton,stopButton)
        }



    }
    companion object {
        private const val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 123
    }
    private fun startRecordingTimer() {
        if (timerJob != null) timerJob?.cancel()
        timerJob = lifecycleScope.launch {
            updateRecordingTime()
        }
    }

    private fun stopRecordingTimer() {
        if (timerJob != null) timerJob?.cancel()
        time = 0
    }

    private suspend fun updateRecordingTime() {
        while (coroutineContext[Job]?.isActive == true) {
            withContext(Dispatchers.Main) {
                textProcess.text = formatDuration(time)
            }
            delay(1000)
            time += 1
        }
    }
    fun getAudio():String?{
        return audioDesc
    }
    private fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, remainingSeconds)
    }
    fun setStartButtonToStop(newButton: View,hideButton: View){
        hideButton.animate()
            .translationYBy(hideButton.height.toFloat())
            .alpha(0.0f)
            .setDuration(500)
            .withEndAction {
                hideButton.visibility = View.GONE
            }

        newButton.visibility = View.VISIBLE
        newButton.alpha = 0.0f

        newButton.animate()
            .translationYBy(-newButton.height.toFloat())
            .alpha(1.0f).duration = 500
    }
    fun clearAudioPath(){
        if (audioDesc != null ) {
            profileViewModel.deleteSoundFromEvent(audioDesc!!)
            audioDescToNull()
        }
    }

    override fun onResume() {
        clearAudioPath()
        super.onResume()
    }
    fun audioDescToNull(){
        audioDesc = null
    }

}