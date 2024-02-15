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
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.ui.MainActivity
import com.example.myapplication.databinding.FragmentChatBinding
import com.example.myapplication.models.MessageInChat
import com.example.myapplication.ui.messages.rcview.MessageListAdapter
import com.example.myapplication.ui.messages.rcview.MessagesPagingAdapter
import com.example.myapplication.ui.messages.viewmodel.MessagesViewModel
import com.example.myapplication.util.Constance
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
class ChatFragment : Fragment() {
    private lateinit var userId: String
    private lateinit var chatId: String


    private val messageViewModel: MessagesViewModel by activityViewModels()

//    lateinit var adapter: MessageListAdapter
    lateinit var pagingAdapter: MessagesPagingAdapter
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



        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

//        adapter = MessageListAdapter(userId, binding.RcViewMesseges)
        pagingAdapter = MessagesPagingAdapter(userId)
        binding.RcViewMesseges.adapter = pagingAdapter
        binding.RcViewMesseges.layoutManager = layoutManager

        val messages = messageViewModel.initMessages(chatId)

//        lifecycleScope.launchWhenStarted {
//            messageViewModel.messages.onEach { messageInChat ->
//                Log.d("MyLog","emit message ")
//                 adapter.addListData(messageInChat)
//            }.collect()
//        }
        lifecycleScope.launch {
            messages.collectLatest(pagingAdapter::submitData)
        }




        binding.floatingActionButton.setOnClickListener {
            val mesId = UUID.randomUUID().toString()
            val currentTime = System.currentTimeMillis()
            val textForMessage = binding.editTextTextMultiLine.text.toString()
            val newMessageForMe = MessageInChat(mesId, userId, chatId, textForMessage, currentTime)
            viewLifecycleOwner.lifecycleScope.launch {
                val response = messageViewModel.testMes(newMessageForMe)
                if(response == Constance.SUCCESS){

                    val currentData = pagingAdapter.snapshot().items

                    val newData = currentData.toMutableList().apply {
                        add(currentData.size -1, newMessageForMe)
                    }

                    // Обновить данные в адаптере
                    pagingAdapter.submitData(PagingData.from(newData))


                }
            }
            binding.editTextTextMultiLine.text.clear()
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

    companion object {
        const val userIdKey = "id"
        const val chatIdKey = "id_chat"
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (activity as MainActivity).showBottomNavigationBar()
    }


}