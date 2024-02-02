package com.example.myapplication.profile

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import java.io.File
import java.io.IOException
import java.util.UUID

class AudioRecorder(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null

    @Suppress("DEPRECATION")
    fun startRecording(): String {
        // Проверяем что запись не выполняется в данный момент
        if (mediaRecorder != null) {
            stopRecording()
        }

        val audioFilePath =
            "${context.filesDir}/audio_${UUID.randomUUID()}.3gp"

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFilePath)

            try {
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("AudioRecorder", "Ошибка при подготовке к записи: ${e.message}")
            }
        }

        return audioFilePath
    }

    fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: RuntimeException) {
            Log.e("AudioRecorder", "Ошибка при остановке записи: ${e.message}")
        }
        mediaRecorder = null
    }

}