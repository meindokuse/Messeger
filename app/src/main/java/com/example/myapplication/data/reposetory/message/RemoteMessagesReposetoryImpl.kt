package com.example.myapplication.data.reposetory.message


import android.net.Uri
import android.util.Log
import com.example.myapplication.data.models.remote.MessageDto
import com.example.myapplication.data.remote.FirebaseStorage
import com.example.myapplication.data.remote.MessagesSocket
import com.example.myapplication.domain.reposetory.message.RemoteMessagesReposetory
import com.example.myapplication.models.MessageInChat
import com.example.myapplication.util.api.NetworkApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class RemoteMessagesReposetoryImpl(
    private val userApi: NetworkApi,
    private val firebaseStorage: FirebaseStorage,
    private val messagesSocket: MessagesSocket
) : RemoteMessagesReposetory {
    override suspend fun getListMessages(
        chatId: String,
        page: Int,
        pageSize: Int
    ): List<MessageDto> {
        return userApi.getAllMessage(chatId, page, pageSize).body()?.messages?.map { message ->
            if (message.type == 2) {
                Log.d("MyLog", "data audio")
                try {
                    val data = firebaseStorage.getData(chatId, message.content) ?: "no_data"
                    Log.d("MyLog", "data audio $data , ${message.content}")
                    message.copy(content = data.toString())
                } catch (e: Exception) {
                    message
                }
            } else message
        } ?: emptyList()
    }

    override suspend fun createNewMessage(idChat: String, message: MessageInChat): Boolean {
        return messagesSocket.sendMessage(idChat, message)
    }

    suspend fun createNewVoiceMessage(
        idChat: String,
        message: MessageInChat,
        tempFile: Uri
    ): Boolean {
        return try {

            firebaseStorage.loadData(idChat, tempFile, message.content)
            val serverResult = messagesSocket.sendMessage(idChat, message)

            serverResult

        } catch (e: Exception) {
            Log.d("MyLog", "error for create message $e")
            false
        }
    }

    suspend fun connectSocket(idChat: String) {
        messagesSocket.connect(idChat)
    }

    fun observeMessages(chatId: String,userId:String): Flow<MessageInChat> {
        return messagesSocket.observeMessages().map { message ->
            if (message.type == 2 && message.id_sender != userId ) {
                Log.d("MyLog", "Mapping mes ${message.content}")
                val data = firebaseStorage.getData(chatId, message.content)
                message.copy(content = data.toString())
            } else message
        }
    }

    fun disconnectSocket() {
        messagesSocket.disconnect()
    }

}