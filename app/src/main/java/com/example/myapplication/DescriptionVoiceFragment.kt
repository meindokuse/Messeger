package com.example.myapplication

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.myapplication.profile.MyViewModelFactory
import com.example.myapplication.reposetory.LocalReposetoryHelper
import com.example.myapplication.viewmodel.MyViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.Manifest
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_description_voice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startOrStopButton = view.findViewById<FloatingActionButton>(R.id.startOrPauseRecordingButton)
        val stopButton = view.findViewById<FloatingActionButton>(R.id.stopButton)
        textProcess = view.findViewById(R.id.durationText)
        stopButton.isEnabled = false
        textProcess.text = "Запись не идет"
        formatDuration(0)

        startOrStopButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // Если нет, то запрашиваем разрешение
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_REQUEST_CODE)
            } else {
                // Если разрешение уже есть, выполняем запись
               audioDesc = profileViewModel.startRecording()
                stopButton.isEnabled = true
                profileViewModel.isRecording.value = true
                startRecordingTimer()
            }
        }
        stopButton.setOnClickListener {
            profileViewModel.stopRecording()
            profileViewModel.isRecording.value = false
            stopRecordingTimer()
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
}