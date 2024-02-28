package com.example.myapplication.ui.messages.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.myapplication.data.remote.MessagesPageSource
import com.example.myapplication.data.reposetory.message.RemoteMessagesReposetoryImpl
import com.example.myapplication.models.MessageInChat
import com.example.myapplication.util.AudioRecorder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val remoteMessagesReposetory: RemoteMessagesReposetoryImpl
) : ViewModel() {

    private var audioRecorder:AudioRecorder? = null

    fun initMessages(chatId: String): Flow<PagingData<MessageInChat>> {
        return Pager(
            PagingConfig(pageSize = 20),
            pagingSourceFactory = { MessagesPageSource(remoteMessagesReposetory,chatId) }
        ).flow.cachedIn(viewModelScope)
    }

     fun sendNewTextMessage(idChat: String, message: MessageInChat){
         viewModelScope.launch(Dispatchers.IO){
             remoteMessagesReposetory.createNewMessage(idChat, message)
         }
     }

    fun sendNewVoiceMessage(idChat: String, message: MessageInChat){
        val tempFile = audioRecorder?.stopRecording()
        viewModelScope.launch(Dispatchers.IO){
            val res = remoteMessagesReposetory.createNewVoiceMessage(idChat, message, tempFile!!)
            Log.d("MyLog","sendNewVoiceMessage $res")
            audioRecorder?.clearRecording()
        }
    }

    fun startRecordVoice(context: Context) {
        audioRecorder = AudioRecorder(context)
        audioRecorder?.startRecording()
    }

    fun cancelRecordVoice(){
        audioRecorder?.clearRecording()
        audioRecorder = null
    }


    suspend fun connectWebSocket(idChat: String) {
        remoteMessagesReposetory.connectSocket(idChat)
    }

    fun observeMessages(chatId: String): Flow<MessageInChat> {
        return remoteMessagesReposetory.observeMessages(chatId)
    }

     fun disconnect() {
        remoteMessagesReposetory.disconnectSocket()
    }

}