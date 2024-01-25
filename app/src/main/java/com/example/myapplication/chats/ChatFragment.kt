package com.example.myapplication.chats

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.FragmentChatBinding
import java.util.UUID


class ChatFragment : Fragment() {
    lateinit var userId:String
    lateinit var chatId:String
    val listTextForMessage = arrayListOf("Привет","Как Дела","Как ты?")
    lateinit var adapter: MessageListAdapter
    lateinit var binding:FragmentChatBinding

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

    private fun init(){
        chatId = arguments?.getString(chatIdKey).toString()
        userId = arguments?.getString(userIdKey).toString()
        Log.d("MyLog",userId.toString())

        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = false
        layoutManager.stackFromEnd = true

        adapter = MessageListAdapter(userId,binding.RcViewMesseges)
        binding.RcViewMesseges.adapter = adapter
        binding.RcViewMesseges.layoutManager = layoutManager

        addMessages(userId)

        binding.floatingActionButton.setOnClickListener {
            val mesId = UUID.randomUUID().toString()
            val currentTime = System.currentTimeMillis()
            val textForMessage = binding.editTextTextMultiLine.text.toString()
            val newMessageForMe = MessageInChat(mesId,userId,chatId,textForMessage,currentTime)
            adapter.addData(newMessageForMe)
            binding.editTextTextMultiLine.text.clear()
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                findNavController().navigateUp()
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }
    companion object{
        const val userIdKey = "id"
        const val chatIdKey = "id_chat"
    }

    //for test
    fun addMessages(userId:String){
        for(i in 0..<listTextForMessage.size){
            val mesId = UUID.randomUUID().toString()
            val currentTime = System.currentTimeMillis()
            val newMessage = MessageInChat(mesId,"qwe",chatId,listTextForMessage[i],currentTime)
            adapter.addData(newMessage)
            val newMessageForMe = MessageInChat(mesId,userId,chatId,listTextForMessage[i],currentTime)
            adapter.addData(newMessageForMe)

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()

        (activity as MainActivity).showBottomNavigationBar()
    }



}