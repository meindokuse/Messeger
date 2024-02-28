package com.example.myapplication.util

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import java.io.File
import java.net.URI
import java.util.Locale
import kotlin.coroutines.coroutineContext

class AudioRecorder(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var tempFile: File? = null

    @Suppress("DEPRECATION")
    fun startRecording() {
        // Проверяем что запись не выполняется в данный момент
        if (mediaRecorder != null) {
            stopRecording()
        }

        try {
            tempFile = File(context.externalCacheDir, "audio_${UUID.randomUUID()}")
            tempFile?.absolutePath?.let { audioFilePath ->
                mediaRecorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    setOutputFile(audioFilePath)

                    try {
                        prepare()
                        start()
                    } catch (e: IOException) {
                        Log.e("MyLog", "Ошибка при подготовке к записи: ${e.message}")
                    }
                }
            }
            Log.d("MyLog","fuf $tempFile")
        } catch (e: IOException) {
            Log.e("MyLog", "Ошибка при создании временного файла: ${e.message}")
        }

    }

    fun stopRecording():Uri? {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: RuntimeException) {
            Log.e("AudioRecorder", "Ошибка при остановке записи: ${e.message}")
        }
        mediaRecorder = null
        return tempFile?.toUri()
    }




    fun clearRecording() {
        Log.d("MyLog","clearRecording")
        tempFile?.delete()
        mediaRecorder?.release()
    }
}
