package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.myapplication.databinding.FragmentAddNewChatBinding
import com.example.myapplication.profile.MyViewModelFactory
import com.example.myapplication.reposetory.LocalReposetoryHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.UUID

class AddNewChatFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentAddNewChatBinding
    private var SelecteValue = 0
    private var listWhoGet = arrayListOf<String>()
    val ChatViewModel: ViewModelForChats by  activityViewModels{
        ChatsViewModelFactory(LocalReposetoryHelper(requireContext()))
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddNewChatBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        val Title = binding.WhoGetText.text.toString()
        binding.MessageText.isEnabled = SelecteValue>0
        binding.toSendButton.setOnClickListener {
            if (!Empty()){
                val message = binding.MessageText.text.toString()
                if(listWhoGet.size>1){
                    for (i in listWhoGet ){
                        val bitmap: Bitmap =
                            BitmapFactory.decodeResource(resources, R.drawable.profile_foro)
                        val uniqueKey = UUID.randomUUID().toString()
                        val currentTime = System.currentTimeMillis()
                        ChatViewModel.AddNewChat(uniqueKey, bitmap,i,message,currentTime)
                    }
                    dismiss()

                }
                else {
                    val bitmap: Bitmap =
                        BitmapFactory.decodeResource(resources, R.drawable.profile_foro)
                    val uniqueKey = UUID.randomUUID().toString()
                    val currentTime = System.currentTimeMillis()
                    ChatViewModel.AddNewChat(uniqueKey, bitmap,listWhoGet[0],message,currentTime)
                    dismiss()
                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = AddNewChatFragment()

    }
    fun init(){

        val Title = binding.WhoGetText.text.toString()
        binding.apply {
            button.setOnClickListener {
                binding.MessageText.isEnabled = SelecteValue>0
                val Name = button.text.toString()
                SelecteValue += 1
                listWhoGet.add(Name)
                binding.WhoGetText.text = "$Title $listWhoGet"

            }
            button2.setOnClickListener {
                binding.MessageText.isEnabled = false
                val Name = button2.text.toString()
                SelecteValue += 1
                listWhoGet.add(Name)
                binding.WhoGetText.text = "$Title $listWhoGet"

            }
            button3.setOnClickListener {
                binding.MessageText.isEnabled = true
                val Name = button3.text.toString()
                SelecteValue += 1
                listWhoGet.add(Name)
                binding.WhoGetText.text = "$Title $listWhoGet"

            }
        }
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


}