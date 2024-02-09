package com.example.myapplication.ui.messages.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.RetrofitStorage
import com.example.myapplication.data.reposetory.message.RemoteMessagesReposetoryImpl
import com.example.myapplication.domain.reposetory.message.RemoteMessagesReposetory
import com.example.myapplication.models.MessageInChat
import com.example.myapplication.util.Constance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val remoteMessagesReposetory: RemoteMessagesReposetoryImpl
):ViewModel() {

    private val _listMessages: MutableStateFlow<Set<MessageInChat>> = MutableStateFlow(emptySet())
    val listMessages: StateFlow<Set<MessageInChat>> = _listMessages

    fun sendMessage(message: MessageInChat) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = remoteMessagesReposetory.createNewMessage(message)
            if (response == Constance.SUCCESS) {
                _listMessages.emit(
                    _listMessages.value.toMutableSet().apply { add(message) })
            }
        }
    }

    suspend fun getAllMessages(idChat: String) {
        val list = remoteMessagesReposetory.getListMessages(idChat)
        if (list != null) _listMessages.emit(
            _listMessages.value.toMutableSet().apply { addAll(list) })
    }

}