package com.example.myapplication.ui.chats.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.myapplication.data.remote.ChatsPagingSource
import com.example.myapplication.data.remote.MessagesPageSource
import com.example.myapplication.util.api.DataForCreateChat
import com.example.myapplication.models.ItemChat
import com.example.myapplication.models.MessageInChat
import com.example.myapplication.models.UserForChoose
import com.example.myapplication.domain.reposetory.chats.RemoteChatsReposetory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
open class ViewModelForChats @Inject constructor(
    private val remoteChatsReposetory: RemoteChatsReposetory,
) : ViewModel() {

    private val _listOfChats: MutableStateFlow<List<ItemChat>> = MutableStateFlow(emptyList())
    val listOfChats: StateFlow<List<ItemChat>> = _listOfChats


    private val _usersForNewChat: MutableStateFlow<List<UserForChoose>> =
        MutableStateFlow(emptyList())
    val usersForNewChat: StateFlow<List<UserForChoose>> = _usersForNewChat


//    suspend fun syncChats(userId: String) {
//        val chats = remoteChatsReposetory.getAllChats(userId)
//        if (chats != null) _listOfChats.emit(chats)
//    }
    fun initChats(userId: String): Flow<PagingData<ItemChat>> {
        return Pager(
            PagingConfig(pageSize = 5),
            pagingSourceFactory = { ChatsPagingSource(remoteChatsReposetory, userId) }
        ).flow.cachedIn(viewModelScope)
    }

    fun createNewChats(

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
            _listOfChats.emit(NewChatsList)
        }
    }


    suspend fun getUsersForNewChat(userId: String) {
        Log.d("MyLog","getUsersForNewChat in viewModel $userId")
        val users = remoteChatsReposetory.getUserForCreateChat(userId)
        if (users != null){
            _usersForNewChat.emit(users)
        }

    }

    fun deleteChat(chats: List<ItemChat>) {
        viewModelScope.launch(Dispatchers.IO) {
            _listOfChats.emit(chats)
        }
    }

}