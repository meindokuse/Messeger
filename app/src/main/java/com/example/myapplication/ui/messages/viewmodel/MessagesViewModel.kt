package com.example.myapplication.ui.messages.viewmodel

import android.content.Context
import android.net.Uri
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
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val remoteMessagesReposetory: RemoteMessagesReposetoryImpl
) : ViewModel() {

    private var audioRecorder: AudioRecorder? = null

    fun initMessages(chatId: String): Flow<PagingData<MessageInChat>> {
        return Pager(
            PagingConfig(pageSize = 20),
            pagingSourceFactory = { MessagesPageSource(remoteMessagesReposetory, chatId) }
        ).flow.cachedIn(viewModelScope)
    }

    fun sendNewTextMessage(idChat: String, message: MessageInChat) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteMessagesReposetory.createNewMessage(idChat, message)
        }
    }

    fun sendNewVoiceMessage(idChat: String, message: MessageInChat, fileUri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = remoteMessagesReposetory.createNewVoiceMessage(idChat, message, fileUri!!)
            audioRecorder?.clearMediaRecorder()
        }

    }

    fun stopRecording(){
        audioRecorder?.stopRecording()
    }

    fun startRecordVoice(context: Context): File? {
        audioRecorder = AudioRecorder(context)
        return audioRecorder?.startRecording()
    }

    fun cancelRecordVoice() {
        audioRecorder?.clearRecordingFull()
        audioRecorder = null
    }


    suspend fun connectWebSocket(idChat: String) {
        remoteMessagesReposetory.connectSocket(idChat)
    }

    fun observeMessages(chatId: String,userId:String): Flow<MessageInChat> {
        return remoteMessagesReposetory.observeMessages(chatId,userId)
    }

    fun disconnect() {
        remoteMessagesReposetory.disconnectSocket()
    }

    fun clearVoiceCache() {
        audioRecorder?.clearCache()
    }

}