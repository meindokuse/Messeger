package com.example.myapplication.data.reposetory.message


import android.net.Uri
import android.util.Log
import com.example.myapplication.data.remote.FirebaseStorage
import com.example.myapplication.data.remote.MessagesSocket
import com.example.myapplication.data.remote.RetrofitStorage
import com.example.myapplication.domain.reposetory.message.RemoteMessagesReposetory
import com.example.myapplication.models.MessageInChat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class RemoteMessagesReposetoryImpl(
    private val firebaseStorage: FirebaseStorage,
    private val messagesSocket: MessagesSocket
) : RemoteMessagesReposetory {
    override suspend fun getListMessages(
        chatId: String,
        page: Int,
        pageSize: Int
    ): List<MessageInChat>? {
        return RetrofitStorage.getAllMessage(chatId, page, pageSize)?.map { message ->
            if (message.type == 2) {
                Log.d("MyLog","data audio")
                try {
                    val data = firebaseStorage.getData(chatId, message.content) ?: "no_data"
                    Log.d("MyLog", "data audio $data")
                    message.copy(content = data.toString())
                } catch (e: Exception) {
                    message
                }
            } else message
        }
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
            Log.d("MyLog", "reading audio for $tempFile")

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

    fun observeMessages(chatId: String): Flow<MessageInChat> {
        return messagesSocket.observeMessages().map { message->
            if (message.type == 2){
               val data = firebaseStorage.getData(chatId,message.content)
                message.copy(content = data.toString())
            } else message
        }
    }

    fun disconnectSocket() {
        messagesSocket.disconnect()
    }

}