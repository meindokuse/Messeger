package com.example.myapplication.ui.messages.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.myapplication.data.remote.MessagesPageSource
import com.example.myapplication.data.remote.RetrofitStorage
import com.example.myapplication.data.reposetory.message.RemoteMessagesReposetoryImpl
import com.example.myapplication.domain.reposetory.message.RemoteMessagesReposetory
import com.example.myapplication.models.MessageInChat
import com.example.myapplication.util.Constance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val remoteMessagesReposetory: RemoteMessagesReposetoryImpl
) : ViewModel() {


    fun initMessages(chatId: String): Flow<PagingData<MessageInChat>> {
        return Pager(
            PagingConfig(pageSize = 20),
            pagingSourceFactory = { MessagesPageSource(chatId) }
        ).flow.cachedIn(viewModelScope)
    }

    suspend fun sendNewMessage(idChat: String, message: MessageInChat): Boolean =
        withContext(Dispatchers.IO) {
            remoteMessagesReposetory.createNewMessage(idChat, message)
        }

    suspend fun connectWebSocket(idChat: String) {
        remoteMessagesReposetory.connectSocket(idChat)
    }

    fun observeMessages(): Flow<MessageInChat> {
        return remoteMessagesReposetory.observeMessages()
    }

     fun disconnect() {
        remoteMessagesReposetory.disconnectSocket()
    }

}