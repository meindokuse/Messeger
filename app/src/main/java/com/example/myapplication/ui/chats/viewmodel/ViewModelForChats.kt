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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
open class ViewModelForChats @Inject constructor(
    private val remoteChatsReposetory: RemoteChatsReposetory,
) : ViewModel() {

    private val _usersForNewChat: MutableStateFlow<List<UserForChoose>> =
        MutableStateFlow(emptyList())
    val usersForNewChat: StateFlow<List<UserForChoose>> = _usersForNewChat

    private val _newChats:MutableStateFlow<List<ItemChat>> =
        MutableStateFlow(emptyList())
    val newChats: StateFlow<List<ItemChat>> = _newChats


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
        val newChatsList = ArrayList<ItemChat>()

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
                    currentTime,
                    1
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
                if (response != null) newChatsList.add(response)
            }
           _newChats.emit(newChatsList)
        }
    }


    suspend fun getUsersForNewChat(userId: String) {
        Log.d("MyLog","getUsersForNewChat in viewModel $userId")
        val users = remoteChatsReposetory.getUserForCreateChat(userId)
        if (users != null){
            _usersForNewChat.emit(users)
        }
    }

    suspend fun deleteChat(chatsForDelete: List<String>):Boolean =
        withContext(Dispatchers.IO) {
            remoteChatsReposetory.deleteChats(chatsForDelete)
        }


}