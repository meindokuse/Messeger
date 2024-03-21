package com.example.myapplication.util

import android.content.Context
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.coroutineContext

class Timer{

    suspend fun updateRecordingTime(textView:TextView) {
        var time = 0
        while (coroutineContext[Job]?.isActive == true) {
            withContext(Dispatchers.Main) {
                textView.text = formatDuration(time)
            }
            delay(1000)
            time += 1
        }
    }
    private fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, remainingSeconds)
    }

}