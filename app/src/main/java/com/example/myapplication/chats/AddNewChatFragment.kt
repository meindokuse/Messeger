package com.example.myapplication.chats


import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.SharedViewModel
import com.example.myapplication.SharedViewModelFactory
import com.example.myapplication.UniversalAdapter
import com.example.myapplication.viewmodel.ViewModelForChats
import com.example.myapplication.databinding.FragmentAddNewChatBinding
import com.example.myapplication.elements.UserForChoose
import com.example.myapplication.profile.rcview.ItemListener
import com.example.myapplication.reposetory.LocalReposetoryHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import com.example.myapplication.constanse
import com.example.myapplication.databinding.FragmentChatBinding
import com.example.myapplication.databinding.ListOfChatsBinding
import java.util.Locale

class AddNewChatFragment() : BottomSheetDialogFragment() {
    private var usersNames = arrayListOf("Паша","Гриша","Евгений","Рома")
    private var usersAvatars = arrayListOf(
        R.drawable.people_first,
        R.drawable.people_second,
        R.drawable.people_third,
        R.drawable.people_four)
    private lateinit var usersAdapter: UniversalAdapter<UserForChoose>

    lateinit var fullList: List<UserForChoose>

    private val selectedUsers = mutableListOf<UserForChoose>()



    private lateinit var binding : FragmentAddNewChatBinding

    val ChatViewModel: ViewModelForChats by  activityViewModels{
        ChatsViewModelFactory(LocalReposetoryHelper(requireContext()),requireActivity().application)
    }
    private val globalViewModel: SharedViewModel by activityViewModels{
        SharedViewModelFactory(LocalReposetoryHelper(requireContext()))
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
                        val singleUser = selectedUsers[0]
                        ChatViewModel.AddNewChat(requireContext(),singleUser,message)
                    }
                    dismiss()
                }
            }
        }


        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                searchUsers(searchText)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    fun init(){

        binding.MessageText.isEnabled = false


        usersAdapter = UniversalAdapter(object: ItemListener{

            override fun onClickDelete(position: Int) {
                val user = usersAdapter.getAllItems()[position]
                usersAdapter.toggleSelection(position)

                if (user !in selectedUsers) {
                    selectedUsers.add(user)
                } else selectedUsers.remove(user)
                updateWhoGetText()
            }

            override fun onClickStartListen(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onClickStopListen(position: Int) {
                TODO("Not yet implemented")
            }

        } , constanse.KEY_FOR_USERS)


        binding.ListUsers.adapter = usersAdapter
        binding.ListUsers.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        addPeople()

        fullList = ArrayList(usersAdapter.getAllItems())

        globalViewModel.isKeyBoardActive.observe(viewLifecycleOwner){
            Log.d("MyLog","Диалог отследил изменения")
            if (it != null && !binding.MessageText.isFocused) {
                Log.d("MyLog",it.toString())
                setupKeyboardListener(it)
            }
        }

    }
    fun Empty() : Boolean {
        binding.apply {

            if(MessageText.text.isNullOrEmpty()) {
                MessageText.error = "Заполните поле"
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

    fun searchUsers(query:String){
        val filteredList = ArrayList<UserForChoose>()

            for (user in fullList) {
                if (user.nickname.toLowerCase(Locale.getDefault())
                    .contains(query.toLowerCase(Locale.getDefault()))
                ) {
                    filteredList.add(user)
                }
            }
        usersAdapter.updateList(filteredList)
        if(filteredList.size == 0){
            binding.ListUsers.visibility = View.GONE
            binding.nullListText.visibility = View.VISIBLE
        } else {
            binding.ListUsers.visibility = View.VISIBLE
            binding.nullListText.visibility = View.GONE

        }

    }


    //FOR TEST
    fun addPeople(){
        for(i in 0..<usersNames.size){
            val userForChoose = UserForChoose("qwe",usersAvatars[i],usersNames[i])
            usersAdapter.addData(userForChoose)
        }
    }



    // ПОЗЖЕ ДОДЕЛАТЬ
    fun setupKeyboardListener(isKeyboardVisible: Boolean) {
        if (isKeyboardVisible) {
            // Обработка появления клавиатуры
            val params = binding.ListUsers.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = resources.getDimensionPixelSize(R.dimen.keyboard_height)
            binding.ListUsers.layoutParams = params
        } else {
            // Обработка скрытия клавиатуры
            val params = binding.ListUsers.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = 0
            binding.ListUsers.layoutParams = params
        }
    }





}

