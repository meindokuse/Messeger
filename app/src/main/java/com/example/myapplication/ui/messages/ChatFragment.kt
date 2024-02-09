package com.example.myapplication.ui.messages

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.ui.MainActivity
import com.example.myapplication.databinding.FragmentChatBinding
import com.example.myapplication.models.MessageInChat
import com.example.myapplication.ui.messages.rcview.MessageListAdapter
import com.example.myapplication.ui.messages.viewmodel.MessagesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
class ChatFragment : Fragment() {
    private lateinit var userId: String
    private lateinit var chatId: String


    private val messageViewModel: MessagesViewModel by activityViewModels()

    lateinit var adapter: MessageListAdapter
    lateinit var binding: FragmentChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
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
        chatId = arguments?.getString(chatIdKey).toString()
        userId = arguments?.getString(userIdKey).toString()
        Log.d("MyLog", userId)

        lifecycleScope.launch {
            messageViewModel.getAllMessages(chatId)
        }

        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = false
        layoutManager.stackFromEnd = true

        adapter = MessageListAdapter(userId, binding.RcViewMesseges)
        binding.RcViewMesseges.adapter = adapter
        binding.RcViewMesseges.layoutManager = layoutManager

        lifecycleScope.launchWhenStarted {
            messageViewModel.listMessages.onEach { messageInChats ->
                adapter.addListData(messageInChats.toList())
            }.collect()
        }



        binding.floatingActionButton.setOnClickListener {
            val mesId = UUID.randomUUID().toString()
            val currentTime = System.currentTimeMillis()
            val textForMessage = binding.editTextTextMultiLine.text.toString()
            val newMessageForMe = MessageInChat(mesId, userId, chatId, textForMessage, currentTime)
            viewLifecycleOwner.lifecycleScope.launch {
                messageViewModel.sendMessage(newMessageForMe)
            }
            adapter.addData(newMessageForMe)
            binding.editTextTextMultiLine.text.clear()
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }

    companion object {
        const val userIdKey = "id"
        const val chatIdKey = "id_chat"
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (activity as MainActivity).showBottomNavigationBar()
    }


}