package com.example.myapplication.ui.messages.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.myapplication.data.local.db.DataBase
import com.example.myapplication.data.reposetory.message.MessagesMediator
import com.example.myapplication.data.reposetory.message.RemoteMessagesReposetoryImpl
import com.example.myapplication.models.MessageInChat
import com.example.myapplication.domain.mapers.toMessageInChat
import com.example.myapplication.util.AudioRecorder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val remoteMessagesReposetory: RemoteMessagesReposetoryImpl,
    private val dataBase: DataBase
) : ViewModel() {

    private var audioRecorder: AudioRecorder? = null

    @OptIn(ExperimentalPagingApi::class)
    fun initMessages(chatId: String):Flow<PagingData<MessageInChat>> {
        return Pager(
            PagingConfig(pageSize = 20),
            pagingSourceFactory = { dataBase.messageDao.getLastMessages() },
            remoteMediator = MessagesMediator(
                dataBase,
                remoteMessagesReposetory,
                chatId
            )
        ).flow.map { it -> it.map { it.toMessageInChat() } }.cachedIn(viewModelScope)
    }

    fun sendNewTextMessage(idChat: String, message: MessageInChat) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteMessagesReposetory.createNewMessage(idChat, message)
        }
    }

    fun sendNewVoiceMessage(idChat: String, message: MessageInChat, fileUri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteMessagesReposetory.createNewVoiceMessage(idChat, message, fileUri!!)
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