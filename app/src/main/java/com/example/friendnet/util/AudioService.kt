package com.example.friendnet.util

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.example.friendnet.ui.MainActivity
import java.lang.ref.WeakReference

class AudioService() : Service() {

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): AudioService {
            return this@AudioService
        }
    }

    private var callback: ServiceCallback? = null

    fun registerCallback(listener: ServiceCallback) {
        this.callback = listener

    }

    fun unregisterCallback() {
        this.callback = null
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private val mediaHandler = Handler(Looper.myLooper()!!)

    private val updateRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayer?.isPlaying == true) {
                callback?.updateProgressUiHolder(mediaPlayer!!.currentPosition)
                mediaHandler.postDelayed(this, 100)
            }

        }
    }


    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }


    fun clearMedia() {

        callback?.updateUiHolderComplete()
        callback?.hidePanel()

        mediaPlayer?.reset()
        mediaPlayer?.release()

        mediaPlayer = null
    }

    fun startPlayer(title: String, mediaData: String) {
        mediaPlayer = MediaPlayer()
        callback?.showPanel(title)

        mediaPlayer?.reset()

        mediaPlayer?.apply {
            try {

                setDataSource(mediaData)
                prepare()
                start()
                Log.d("MyLog", "запись ${mediaPlayer?.duration}")

            } catch (e: Exception) {
                Log.d("MyLog", "ошибка при запуске аудио $e")
                stopSelf()
            }
        }
        mediaPlayer?.setOnCompletionListener {
            clearMedia()
            stopSelf()
            mediaHandler.removeCallbacksAndMessages(null)
        }
        mediaHandler.post(updateRunnable)
    }

    fun resumePlayer() {
        mediaPlayer?.start()
        mediaHandler.post(updateRunnable)
    }

    fun pausePlayer() {
        mediaHandler.removeCallbacksAndMessages(null)
        mediaPlayer?.pause()
    }


    fun checkIsPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun getDurationMedia(): Int = mediaPlayer!!.duration

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyLog", "onDestroy")
        clearMedia()

    }






}