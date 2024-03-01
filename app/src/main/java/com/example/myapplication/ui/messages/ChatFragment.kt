package com.example.myapplication.ui.messages

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.ui.MainActivity
import com.example.myapplication.databinding.FragmentChatBinding
import com.example.myapplication.models.MessageInChat
import com.example.myapplication.ui.messages.rcview.MessagesLoadingState
import com.example.myapplication.ui.messages.rcview.MessagesPagingAdapter
import com.example.myapplication.ui.messages.viewmodel.MessagesViewModel
import com.example.myapplication.ui.profile.editable.DescriptionVoiceFragment
import com.example.myapplication.ui.profile.rcview.ItemListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.coroutineContext

@AndroidEntryPoint
class ChatFragment : Fragment() {
    private lateinit var userId: String
    private lateinit var chatId: String

    private var audioMessage: File? = null

    private val messageViewModel: MessagesViewModel by activityViewModels()

    private var currentlyPlayingViewHolder: MessagesPagingAdapter.AudioMessageHolder? = null

    private lateinit var pagingAdapter: MessagesPagingAdapter
    lateinit var binding: FragmentChatBinding

    private var timerJob: Job? = null

    private var time = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChatBinding.inflate(inflater)

        (activity as MainActivity).hideBottomNavigationBar()

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }


    private fun init() {
        initRecyclerView()
        initData()
        initButtons()

        binding.editTextTextMultiLine.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //ignore
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEmpty = s.isNullOrBlank()
                if (isEmpty) {
                    binding.sendTextMessageButton.visibility = View.GONE
                    binding.sendVoiceMessageButton.visibility = View.VISIBLE

                } else {
                    binding.sendTextMessageButton.visibility = View.VISIBLE
                    binding.sendVoiceMessageButton.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //ignore
            }

        })
    }

    private fun initButtons() {
        binding.sendTextMessageButton.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        binding.sendVoiceMessageButton.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )

        binding.sendTextMessageButton.setOnClickListener {
            val mesId = UUID.randomUUID().toString()
            val currentTime = System.currentTimeMillis()
            val textForMessage = binding.editTextTextMultiLine.text.toString()
            val message = MessageInChat(mesId, userId, chatId, textForMessage, currentTime, 1)
            try {
                val currentData = pagingAdapter.snapshot().items.toMutableList()
                currentData.add(0, message)
                lifecycleScope.launch {
                    pagingAdapter.submitData(PagingData.from(currentData))
                    val position = pagingAdapter.snapshot().indexOf(message)
                    pagingAdapter.toggleWaitingMessage(position)
                    binding.RcViewMesseges.smoothScrollToPosition(0)
                }
                messageViewModel.sendNewTextMessage(chatId, message)
                binding.editTextTextMultiLine.text.clear()
            } catch (e: Exception) {
                showErrorSnackbar(message)
            }

        }

        binding.sendVoiceMessageButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    RECORD_AUDIO_PERMISSION_REQUEST_CODE
                )
            } else {
                try {
                    if (audioMessage == null) {
                        startRecordingTimer()
                        vibrate(requireContext(), 100)
                        changeButtonToRecordMoment()
                        audioMessage = messageViewModel.startRecordVoice(requireContext())

                    } else {
                        stopRecordingTimer()
                        messageViewModel.stopRecording()

                        val mesId = UUID.randomUUID().toString()
                        val currentTime = System.currentTimeMillis()
                        val message = MessageInChat(mesId, userId, chatId, audioMessage!!.toUri().toString(), currentTime, 2)

                        lifecycleScope.launch {
                            val currentData = pagingAdapter.snapshot().items.toMutableList()
                            currentData.add(0, message)
                            pagingAdapter.submitData(PagingData.from(currentData))

                            val position = pagingAdapter.snapshot().indexOf(message)
                            pagingAdapter.toggleWaitingMessage(position)

                            binding.RcViewMesseges.smoothScrollToPosition(0)
                        }

                        messageViewModel.sendNewVoiceMessage(chatId, message,audioMessage?.toUri())
                        changeButtonToNoRecordMoment()
                        audioMessage = null
                    }
                } catch (e: Exception) {
                    Log.d("MyLog", "ошибка записи ${e.message}")
                    audioMessage = null
                    Toast.makeText(requireContext(), "Ошибка записи!", Toast.LENGTH_SHORT).show()
                }

            }

        }

        binding.cancelVoiceButton.setOnClickListener {
            audioMessage = null
            changeButtonToNoRecordMoment()
            messageViewModel.cancelRecordVoice()
        }
    }

    private fun initRecyclerView() {
        chatId = arguments?.getString(chatIdKey).toString()
        userId = arguments?.getString(userIdKey).toString()
        Log.d("MyLog", userId)


        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = false

        pagingAdapter = MessagesPagingAdapter(object : ItemListener {
            override fun onClickDelete(position: Int) {
                //ignore
            }

            override fun onClickStartListen(position: Int, mediaPlayer: MediaPlayer) {
                stopCurrentlyPlaying()
                val viewHolder =
                    binding.RcViewMesseges.findViewHolderForAdapterPosition(position) as? MessagesPagingAdapter.AudioMessageHolder
                viewHolder?.updatePlayButtonImage(true)
                currentlyPlayingViewHolder = viewHolder
            }

            override fun onClickStopListen(position: Int, mediaPlayer: MediaPlayer) {
                currentlyPlayingViewHolder?.updatePlayButtonImage(false)
                currentlyPlayingViewHolder = null
            }

        }, userId)

        binding.RcViewMesseges.adapter = pagingAdapter.withLoadStateHeaderAndFooter(
            header = MessagesLoadingState(),
            footer = MessagesLoadingState(),
        )
        binding.RcViewMesseges.layoutManager = layoutManager

        lifecycleScope.launch {
            pagingAdapter.onPagesUpdatedFlow.collectLatest {
                binding.progressLoading.visibility = View.GONE
            }
        }
    }

    private fun initData() {
        val messages = messageViewModel.initMessages(chatId)

        lifecycleScope.launch {
            messages.collectLatest { pagingData ->
                pagingAdapter.submitData(pagingData)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeButtonToRecordMoment() {
        binding.sendVoiceMessageButton.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.wow_color)
        )
        binding.sendVoiceMessageButton.setImageResource(R.drawable.baseline_send_24)


        binding.editTextTextMultiLine.visibility = View.GONE
        binding.cancelVoiceButton.visibility = View.VISIBLE
        binding.recordTimeText.visibility = View.VISIBLE

        binding.sendVoiceMessageButton.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
    }

    private fun changeButtonToNoRecordMoment() {
        binding.sendVoiceMessageButton.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.default_color_plus_dark)
        )
        binding.sendVoiceMessageButton.setImageResource(R.drawable.ic_micro_24)

        binding.editTextTextMultiLine.visibility = View.VISIBLE
        binding.cancelVoiceButton.visibility = View.GONE
        binding.recordTimeText.visibility = View.GONE

        binding.sendVoiceMessageButton.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            try {
                messageViewModel.connectWebSocket(chatId)
                messageViewModel.observeMessages(chatId,userId).collectLatest { message ->
                    Log.d("MyLog", "connectWebSocket new message $message")
                    val currentData = pagingAdapter.snapshot().items.toMutableList()
                    if (message.id_sender != userId) {
                        currentData.add(0, message)
                        launch {
                            pagingAdapter.submitData(PagingData.from(currentData))
                        }
                    } else {
                        Log.d("MyLog","toggleWaitingMessage new message ")
                        val position = pagingAdapter.snapshot().indexOf(message)
                        pagingAdapter.toggleWaitingMessage(position)
                    }

                }
            } catch (e: Exception) {
                Log.d("MyLog", "connect socket error $e")
            }
        }
    }

    companion object {
        const val userIdKey = "id"
        const val chatIdKey = "id_chat"
        const val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 123
    }

    override fun onStop() {
        super.onStop()
        messageViewModel.disconnect()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).showBottomNavigationBar()
        messageViewModel.clearVoiceCache()
    }

    private fun showErrorSnackbar(messageInChat: MessageInChat) {
        view?.let { view ->
            Snackbar.make(view, "Произошла ошибка при получении чатов", Snackbar.LENGTH_LONG)
                .setAction("Повторить") {
                    lifecycleScope.launch {
                        messageViewModel.sendNewTextMessage(chatId, messageInChat)
                    }
                }
                .show()
        }
    }

    private fun stopCurrentlyPlaying() {
        currentlyPlayingViewHolder?.let {
            it.updatePlayButtonImage(false)
            it.mediaPlayer.pause()
        }
    }

    private fun vibrate(context: Context, duration: Long) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect =
                VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(vibrationEffect)
        } else {
            vibrator.vibrate(duration)
        }
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
                binding.recordTimeText.text = formatDuration(time)
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