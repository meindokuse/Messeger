package com.example.myapplication.util

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import java.io.IOException
import java.util.UUID
import java.io.File

class AudioRecorder(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var tempFile: File? = null

    fun startRecording(): File? {
        // остановить запись если та выполняется
        stopRecording()

        try {
            tempFile = File(context.externalCacheDir, "audio_${UUID.randomUUID()}")
            tempFile?.createNewFile()

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(320000)
                setOutputFile(tempFile?.absolutePath)

                prepare()
                start()
            }

            return tempFile
        } catch (e: IOException) {
            Log.e("AudioRecorder", "Ошибка при записи аудио: ${e.message}")
            return null
        }
    }

    fun stopRecording(): Boolean {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            true
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Ошибка при остановке записи аудио: ${e.message}")
            false
        }
    }

    fun clearMediaRecorder(){
        mediaRecorder = null
    }

    fun clearRecordingFull(){
        mediaRecorder = null
        tempFile?.delete()
    }

    fun clearCache() {
        val cacheDir = context.externalCacheDir
        cacheDir?.let { cache ->
            cache.listFiles()?.forEach { file ->
                if (file.isFile) {
                    Log.d("MyLog","deleted file ${file.absolutePath}")
                    file.delete()
                }
            }
        }
    }
}

