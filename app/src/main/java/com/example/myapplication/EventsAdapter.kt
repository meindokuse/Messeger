package com.example.myapplication

import android.media.MediaPlayer
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.EventItemBinding
import com.example.myapplication.databinding.EventItemSoundVersionBinding
import com.example.myapplication.elements.Event
import com.example.myapplication.profile.rcview.ItemListener
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventsAdapter(val itemListener: ItemListener):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        private const val VIEW_TYPE_TEXT_POST = 1
        private const val VIEW_TYPE_AUDIO_POST = 2
    }

    private val posts = mutableListOf<Event>()

    val mediaPlayers = mutableListOf<MediaPlayer>()
    inner class TextPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = EventItemBinding.bind(itemView)
        fun bind(textPost: Event,position: Int) {
            binding.title.text = "Tема:${textPost.title}"
            binding.description.text = textPost.desc
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val formattedDate = sdf.format(Date(textPost.data))
            binding.DataText.text = formattedDate

            binding.deleteButton.setOnClickListener {
                itemListener.onClickDelete(position)
            }
        }
    }


    inner class AudioPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mediaPlayer = MediaPlayer()
        private val updateHandler = Handler(Looper.getMainLooper())

        val binding = EventItemSoundVersionBinding.bind(itemView)

        init {
            mediaPlayer.setOnPreparedListener {
                val durationInSeconds = mediaPlayer.duration / 1000
                binding.seekBar.max = durationInSeconds
                binding.durationTextView.text = formatDuration(durationInSeconds)
            }
        }

        fun bind(audioPost: Event, position: Int) {
            binding.titleVsound.text = audioPost.title

            binding.deleteButtonVsound.setOnClickListener {
                itemListener.onClickDelete(position)
            }
            binding.playButton.setOnClickListener {
                itemListener.onClickStartListen(position, mediaPlayer)
                binding.playButton.setImageResource(R.drawable.baseline_pause_24)
                mediaPlayer.start()
                updateHandler.post(object : Runnable {
                    override fun run() {
                        if (mediaPlayer.isPlaying) {
                            updateSeekListener(mediaPlayer.currentPosition / 1000)
                            updateHandler.postDelayed(this, 1000)
                        }
                    }
                })
            }
            binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    mediaPlayer.pause()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    val progress = seekBar?.progress ?: 0
                    mediaPlayer.seekTo(progress * 1000)
                    mediaPlayer.start()
                }
            })

            mediaPlayer.setOnSeekCompleteListener {
                updateHandler.post {
                    updateSeekListener(mediaPlayer.currentPosition / 1000)
                }
            }
            mediaPlayer.reset()
            try {
                mediaPlayer.apply {
                    setDataSource(audioPost.desc)
                    prepareAsync()
                }
                mediaPlayers.add(mediaPlayer)
            } catch (e: IOException) {
                Log.e("YourAudioPlaybackClass", "Ошибка при воспроизведении аудио: ${e.message}")
            }
            mediaPlayer.setOnCompletionListener {
                binding.playButton.setImageResource(R.drawable.baseline_play_arrow_24)
                val durationInSeconds = mediaPlayer.duration / 1000
                binding.seekBar.max = durationInSeconds
                binding.durationTextView.text = formatDuration(durationInSeconds)
                binding.seekBar.progress = 0
            }
        }

        fun updateSeekListener(currentPosition: Int) {
            binding.seekBar.progress = currentPosition
            binding.durationTextView.text = formatDuration(currentPosition)
        }

        private fun formatDuration(seconds: Int): String {
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            return String.format(Locale.getDefault(), "%d:%02d", minutes, remainingSeconds)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (posts[position].type == 2) VIEW_TYPE_AUDIO_POST
        else VIEW_TYPE_TEXT_POST
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return when (viewType){

           VIEW_TYPE_TEXT_POST -> {
               val itemView = LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
               TextPostViewHolder(itemView)
           }
           VIEW_TYPE_AUDIO_POST ->{
               val itemView = LayoutInflater.from(parent.context).inflate(R.layout.event_item_sound_version,parent,false)
               AudioPostViewHolder(itemView)
           }
           else -> throw IllegalArgumentException("Unknown view type")       }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType){
            VIEW_TYPE_TEXT_POST -> {
                val textPostHolder = holder as TextPostViewHolder
                textPostHolder.bind(posts[position],position)
            }
            VIEW_TYPE_AUDIO_POST ->{
                val audioPostHolder = holder as AudioPostViewHolder
                audioPostHolder.bind(posts[position],position)
            }
        }
    }
    fun addListEvent(eventList: List<Event>) {
        posts.addAll(eventList)
        posts.sortByDescending { it.data }
        notifyDataSetChanged()
    }

    fun addEvent(event: Event) {
        posts.add(0, event)
        notifyDataSetChanged()
    }
    fun removeItem(position: Int){
        posts.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount - position)
    }
    fun getAllItems():List<Event>{
        return posts
    }


}