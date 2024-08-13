package com.example.friendnet.data.reposetory.message


import android.net.Uri
import android.util.Log
import com.example.friendnet.data.models.remote.MessageDto
import com.example.friendnet.data.remote.FirebaseStorage
import com.example.friendnet.data.remote.MessagesSocket
import com.example.friendnet.domain.reposetory.message.RemoteMessagesReposetory
import com.example.friendnet.models.MessageInChat
import com.example.friendnet.util.api.NetworkApi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


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
        return userApi.getAllMessage(chatId, page, pageSize).body()?.messages ?: emptyList()
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

    fun observeMessages(chatId: String, userId: String): Flow<MessageInChat> {
        return messagesSocket.observeMessages().map { message ->
            if (message.type == 2 && message.id_sender != userId) {
                Log.d("MyLog", "Mapping mes ${message.content}")
                val data = firebaseStorage.getData(chatId, message.content)
                message.copy(content = data.toString())
            } else message
        }
    }

    suspend fun getRemoteFile(userId: String, fileName: String): Uri? {
        return firebaseStorage.getData(userId, fileName)
    }

    fun disconnectSocket() {
        messagesSocket.disconnect()
    }

}