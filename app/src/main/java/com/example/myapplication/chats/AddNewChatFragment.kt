package com.example.myapplication.chats

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.UniversalAdapter
import com.example.myapplication.viewmodel.ViewModelForChats
import com.example.myapplication.databinding.FragmentAddNewChatBinding
import com.example.myapplication.elements.UserForChoose
import com.example.myapplication.profile.rcview.ItemListener
import com.example.myapplication.reposetory.LocalReposetoryHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import com.example.myapplication.constanse

class AddNewChatFragment : BottomSheetDialogFragment() {
    private var usersNames = arrayListOf("Паша","Гриша","Евгений","Рома")
    private var usersAvatars = arrayListOf(
        R.drawable.people_first,
        R.drawable.people_second,
        R.drawable.people_third,
        R.drawable.people_four)
    private lateinit var usersAdapter: UniversalAdapter<UserForChoose>

    private val selectedUsers = mutableListOf<UserForChoose>()


    private lateinit var binding : FragmentAddNewChatBinding

    val ChatViewModel: ViewModelForChats by  activityViewModels{
        ChatsViewModelFactory(LocalReposetoryHelper(requireContext()),requireActivity().application)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNewChatBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        binding.toSendButton.setOnClickListener {
            lifecycleScope.launch {
                if (!Empty()) {
                    val message = binding.MessageText.text.toString()
                    if (selectedUsers.size > 1) {

                        ChatViewModel.AddChats(requireContext(),selectedUsers,message)

                    } else {

                        ChatViewModel.AddNewChat(requireContext(),selectedUsers,message)

                    }
                    dismiss()
                }
            }
        }
    }

    fun init(){
        binding.MessageText.isEnabled = false


        usersAdapter = UniversalAdapter(object: ItemListener{

            override fun onClick(position: Int) {
                val user = usersAdapter.contentList[position]
                usersAdapter.toggleSelection(position)

                if (user !in selectedUsers) {
                    selectedUsers.add(user)
                } else selectedUsers.remove(user)
                updateWhoGetText()
            }

        } , constanse.KEY_FOR_USERS)

        binding.ListUsers.adapter = usersAdapter
        binding.ListUsers.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        addPeople()

    }
    fun Empty() : Boolean {
        binding.apply {

            if(MessageText.text.isNullOrEmpty()) {
                MessageText.error = "Заполните поле"
                Log.d("MyLog", "DescriptionForEvent is empty")
            }
            return  MessageText.text.isNullOrEmpty()
        }
    }


    private fun updateWhoGetText() {
        if (selectedUsers.isEmpty()){
            binding.MessageText.isEnabled = false
            binding.WhoGetText.text = "Кому отправим?"
        }else {
            val names = mutableListOf<String>()
            selectedUsers.forEach {
                names.add(it.nickname)
            }
            binding.WhoGetText.text = names.joinToString(", ")
            binding.MessageText.isEnabled = true
        }

    }

    //FOR TEST
    fun addPeople(){
        for(i in 0..<usersNames.size){
            val userForChoose = UserForChoose("qwe",usersAvatars[i],usersNames[i])
            usersAdapter.addData(userForChoose)
        }
    }

}