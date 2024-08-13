package com.example.friendnet.ui.messages.rcview

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.friendnet.R
import com.example.friendnet.databinding.MessageBodyBinding
import com.example.friendnet.databinding.VoiceMessageBodyBinding
import com.example.friendnet.models.MessageInChat
import com.example.friendnet.ui.profile.rcview.ItemListener
import com.example.friendnet.util.AudioInterface
import com.example.friendnet.util.AudioService
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashSet
import java.util.Locale

class MessagesPagingAdapter(
    private val itemListener: AudioInterface,
    private val userId: String
) : PagingDataAdapter<MessageInChat, RecyclerView.ViewHolder>(MessageDiffItemCallback) {

    private val waitingMessages = HashSet<Int>()

    companion object{
        private const val VIEW_TYPE_TEXT_POST = 1
        private const val VIEW_TYPE_AUDIO_POST = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_TEXT_POST -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.message_body, parent, false)
                MessageHolder(view)
            }
            VIEW_TYPE_AUDIO_POST -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.voice_message_body, parent, false)
                AudioMessageHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.message_body, parent, false)
                MessageHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessagesPagingAdapter.MessageHolder -> {
                holder.bind(getItem(position),position)
                Log.d("MyLog","messageTextHolder")
            }

            is MessagesPagingAdapter.AudioMessageHolder -> {
                holder.bind(getItem(position),position)
                Log.d("MyLog","audioMessageHolder")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (snapshot().items[position].type == 1) VIEW_TYPE_TEXT_POST else VIEW_TYPE_AUDIO_POST
    }

    fun toggleWaitingMessage(position: Int) {
        if (waitingMessages.contains(position)) {
            waitingMessages.remove(position)
        } else {
            waitingMessages.add(position)
        }
        notifyItemChanged(position)
    }

    private fun isWaiting(position: Int): Boolean {
        return waitingMessages.contains(position)
    }

    inner class AudioMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var exportAudioMessage: MessageInChat? = null

        val binding = VoiceMessageBodyBinding.bind(itemView)

        var isActive = false

        fun initSeekBarProgress(progressMax:Int){
            binding.seekBar.max = progressMax
        }


        @SuppressLint("SetTextI18n")
        fun bind(audioMessage: MessageInChat?,position: Int) {

            binding.durationVoice.text = audioMessage?.duration

            exportAudioMessage = audioMessage
            Log.d("MyLog","bind $position voice $audioMessage")

            binding.statusVoiceMes.visibility = if (isWaiting(position)) View.VISIBLE else View.GONE

            val layoutParams = binding.cardMes.layoutParams as ConstraintLayout.LayoutParams
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val formattedDate = sdf.format(Date(audioMessage!!.time))

           binding.dataSend.text = formattedDate

            if (audioMessage.id_sender == userId) {

                layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET
                binding.cardMes.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.cardMes.context,
                        R.color.white
                    )
                )

            } else {
                // Сообщение не пользователя, отображать слева
                layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
                binding.cardMes.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.cardMes.context,
                        R.color.lite_blue
                    )
                )
            }

            binding.playVoiceButton.setOnClickListener {
                if (itemListener.checkIsPlaying()) {
                    binding.playVoiceButton.setImageResource(R.drawable.baseline_play_arrow_24)
                    itemListener.onClickStopListen(position)

                } else {
                    isActive = true
                    itemListener.onClickStartListen(position)
                    binding.playVoiceButton.setImageResource(R.drawable.baseline_pause_24)
//                    updateHandler.post(updateRunnable)
                }
            }


            binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        binding.durationVoice.text = formatDuration(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                    updateHandler.removeCallbacks(updateRunnable)
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    val progress = seekBar?.progress ?: 0
                    itemListener.seekMediaPlayerTo(progress)
//                    updateHandler.post(updateRunnable)
                }
            })
//            mediaPlayer?.setOnSeekCompleteListener {
//                updateHandler.post {
//                    updateSeekListener(audioService!!.getCurrentPosition())
//                }
//            }

//            mediaPlayer?.setOnCompletionListener {
//                binding.playVoiceButton.setImageResource(R.drawable.baseline_play_arrow_24)
//                val durationInSeconds = mediaPlayer!!.duration
//                binding.seekBar.max = durationInSeconds
//                binding.durationVoice.text = formatDuration(durationInSeconds)
//                binding.seekBar.progress = 0
//                mediaPlayer = null
//            }

        }

//        val updateRunnable = object : Runnable {
//            override fun run() {
//                if (audioService!!.checkIsPlaying()) {
//                    val currentPosition = audioService.getCurrentPosition()
//                    binding.seekBar.progress = currentPosition
//                    binding.durationVoice.text = formatDuration(currentPosition)
//                    updateHandler.postDelayed(this, 100)
//                }
//            }
//        }


        private fun formatDuration(milliseconds: Int): String {
            val totalSeconds = milliseconds / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }

        fun updateProgress(currentPosition: Int){
            binding.seekBar.progress = currentPosition
            binding.durationVoice.text = formatDuration(currentPosition)
        }

        fun updateCompleteProgress(initDuration:Int){
            binding.playVoiceButton.setImageResource(R.drawable.baseline_play_arrow_24)
            binding.seekBar.max = initDuration
            binding.durationVoice.text = formatDuration(initDuration)
            binding.seekBar.progress = 0
            isActive = false
        }

        fun updatePlayButtonImage(isPlaying: Boolean) {
            if (isPlaying) {
                binding.playVoiceButton.setImageResource(R.drawable.baseline_pause_24)
                binding.seekBar.progress = 0
            } else {
                binding.playVoiceButton.setImageResource(R.drawable.baseline_play_arrow_24)
            }
        }

    }

    inner class MessageHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = MessageBodyBinding.bind(item)

        fun bind(messageInChat: MessageInChat?,position: Int) {
            Log.d("MyLog","bind text")

            binding.textMessage.text = messageInChat?.content
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val formattedDate = sdf.format(Date(messageInChat!!.time))
            binding.dataSend.text = formattedDate
            val layoutParams = binding.cardMes.layoutParams as ConstraintLayout.LayoutParams

            if (messageInChat.id_sender == userId) {

                layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET
                binding.cardMes.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.cardMes.context,
                        R.color.white
                    )
                )

            } else {
                // Сообщение не пользователя, отображать слева
                layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
                binding.cardMes.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.cardMes.context,
                        R.color.lite_blue
                    )
                )

            }

            binding.status.visibility = if (isWaiting(position)) View.VISIBLE else View.GONE
        }
    }
}

private object MessageDiffItemCallback : DiffUtil.ItemCallback<MessageInChat>() {

    override fun areItemsTheSame(oldItem: MessageInChat, newItem: MessageInChat): Boolean {
        return oldItem.message_id == newItem.message_id
    }

    override fun areContentsTheSame(oldItem: MessageInChat, newItem: MessageInChat): Boolean {
        return oldItem == newItem
    }
}