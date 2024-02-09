package com.example.myapplication.ui.chats.editable


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.util.SharedViewModel
import com.example.myapplication.util.UniversalAdapter
import com.example.myapplication.ui.chats.rcview.UsersListener
import com.example.myapplication.ui.chats.viewmodel.ViewModelForChats
import com.example.myapplication.databinding.FragmentAddNewChatBinding
import com.example.myapplication.models.UserForChoose
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import com.example.myapplication.util.Constance
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import java.util.Locale

@AndroidEntryPoint
class AddNewChatFragment(private val userId: String) : BottomSheetDialogFragment() {

    private lateinit var usersAdapter: UniversalAdapter<UserForChoose>

    lateinit var fullList: List<UserForChoose>

    private val selectedUsers = mutableListOf<UserForChoose>()


    private lateinit var binding: FragmentAddNewChatBinding

    val ChatViewModel: ViewModelForChats by activityViewModels()
    private val globalViewModel: SharedViewModel by activityViewModels()
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
                    val text = binding.MessageText.text.toString()
                    ChatViewModel.addChats(selectedUsers, text, userId)
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

    fun init() {


        binding.MessageText.isEnabled = false


        usersAdapter = UniversalAdapter(object : UsersListener {
            override fun clickToUser(position: Int) {
                val user = usersAdapter.getAllItems()[position]
                usersAdapter.toggleSelection(position)

                if (user !in selectedUsers) {
                    selectedUsers.add(user)
                } else selectedUsers.remove(user)
                updateWhoGetText()
            }

        }, Constance.KEY_FOR_USERS)


        binding.ListUsers.adapter = usersAdapter
        binding.ListUsers.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        getPeoples()

        fullList = ArrayList(usersAdapter.getAllItems())

        lifecycleScope.launchWhenStarted {
            ChatViewModel.usersForNewChat.onEach { messageInChats ->
                if (messageInChats.isNotEmpty()) {
                    usersAdapter.addListData(messageInChats.toList())
                }
            }.collect()
        }

        globalViewModel.isKeyBoardActive.observe(viewLifecycleOwner) {
            Log.d("MyLog", "Диалог отследил изменения")
            if (it != null && !binding.MessageText.isFocused) {
                Log.d("MyLog", it.toString())
                setupKeyboardListener(it)
            }
        }

    }

    fun Empty(): Boolean {
        binding.apply {

            if (MessageText.text.isNullOrEmpty()) {
                MessageText.error = "Заполните поле"
            }
            return MessageText.text.isNullOrEmpty()
        }
    }


    private fun updateWhoGetText() {
        if (selectedUsers.isEmpty()) {
            binding.MessageText.isEnabled = false
            binding.WhoGetText.text = "Кому отправим?"
        } else {
            val names = mutableListOf<String>()
            selectedUsers.forEach {
                names.add(it.nickname)
            }
            binding.WhoGetText.text = names.joinToString(", ")
            binding.MessageText.isEnabled = true
        }

    }

    fun searchUsers(query: String) {
        val filteredList = ArrayList<UserForChoose>()

        for (user in fullList) {
            if (user.nickname.toLowerCase(Locale.getDefault())
                    .contains(query.toLowerCase(Locale.getDefault()))
            ) {
                filteredList.add(user)
            }
        }
        usersAdapter.updateList(filteredList)
        if (filteredList.size == 0) {
            binding.ListUsers.visibility = View.GONE
            binding.nullListText.visibility = View.VISIBLE
        } else {
            binding.ListUsers.visibility = View.VISIBLE
            binding.nullListText.visibility = View.GONE

        }

    }


    //FOR TEST
    fun getPeoples() {
        lifecycleScope.launch(Dispatchers.IO){
            ChatViewModel.getUsersForNewChat(userId)
        }

    }


    private fun setupKeyboardListener(isKeyboardVisible: Boolean) {
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

