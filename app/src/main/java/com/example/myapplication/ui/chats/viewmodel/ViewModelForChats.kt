package com.example.myapplication.ui.chats.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.util.api.DataForCreateChat
import com.example.myapplication.models.ItemChat
import com.example.myapplication.models.MessageInChat
import com.example.myapplication.models.UserForChoose
import com.example.myapplication.data.remote.RetrofitStorage
import com.example.myapplication.domain.interactor.ChatCases
import com.example.myapplication.domain.reposetory.chats.ChatLocalReposetory
import com.example.myapplication.domain.reposetory.chats.RemoteChatsReposetory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
open class ViewModelForChats @Inject constructor(
    private val remoteChatsReposetory: RemoteChatsReposetory,
) : ViewModel() {

    private val _listOfChats: MutableStateFlow<Set<ItemChat>> = MutableStateFlow(emptySet())
    val listOfChats: StateFlow<Set<ItemChat>> = _listOfChats


    private val _usersForNewChat: MutableStateFlow<Set<UserForChoose>> =
        MutableStateFlow(emptySet())
    val usersForNewChat: StateFlow<Set<UserForChoose>> = _usersForNewChat


//    private suspend fun initChatList() {
//        Log.d("MyLog", "Обновление модели чатов")
//        val savedChats = localReposetoryHelper.GetAllChats()
//        _listOfChats.emit(savedChats)
//    }


    suspend fun syncChats(userId: String) {
        val chats = remoteChatsReposetory.getAllChats(userId)
        if (chats != null) _listOfChats.emit(chats.toSet())
    }

    fun addChats(

        listWhoGetMes: List<UserForChoose>,
        text: String,
        userCreater: String
    ) {
        val NewChatsList = ArrayList<ItemChat>()

        viewModelScope.launch(Dispatchers.IO) {
            for (user in listWhoGetMes) {

                val chatId = UUID.randomUUID().toString()
                val messageId = UUID.randomUUID().toString()
                val currentTime = System.currentTimeMillis()

                val messageInChat = MessageInChat(
                    messageId,
                    userCreater,
                    chatId,
                    text,
                    currentTime
                )
                val itemChat = ItemChat(
                    chatId,
                    userCreater,
                    user.id,
                    user.foto,
                    user.nickname,
                    text,
                    currentTime
                )
                val dataForCreateChat = DataForCreateChat(itemChat, messageInChat)
                val response = remoteChatsReposetory.createNewChat(dataForCreateChat)
                if (response != null) NewChatsList.add(itemChat)
            }
            _listOfChats.emit(_listOfChats.value.toMutableSet().apply { addAll(NewChatsList) })
        }
    }

    suspend fun getUsersForNewChat(userId: String) {
        val users = remoteChatsReposetory.getUserForCreateChat(userId)
        if (users != null){
            _usersForNewChat.emit(
                _usersForNewChat.value.toMutableSet().apply { addAll(users) }
            )
        }

    }

    fun deleteChat(chats: List<ItemChat>) {
        viewModelScope.launch(Dispatchers.IO) {
            _listOfChats.emit(_listOfChats.value.toMutableSet().apply { removeAll(chats.toSet()) })
        }
    }

}