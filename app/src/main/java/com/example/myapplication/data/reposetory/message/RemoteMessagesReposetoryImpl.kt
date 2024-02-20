package com.example.myapplication.data.reposetory.message


import com.example.myapplication.data.remote.MessagesSocket
import com.example.myapplication.data.remote.RetrofitStorage
import com.example.myapplication.domain.reposetory.message.RemoteMessagesReposetory
import com.example.myapplication.models.MessageInChat

import com.example.myapplication.util.Constance


class RemoteMessagesReposetoryImpl(
    private val messagesSocket: MessagesSocket
) : RemoteMessagesReposetory {
    override suspend fun getListMessages(chatId: String): List<MessageInChat>? {
        return RetrofitStorage.getAllMessage(chatId)
    }

    override suspend fun createNewMessage(idChat: String,message: MessageInChat): Boolean {
        return messagesSocket.sendMessage(idChat, message)
    }

    suspend fun connectSocket(idChat: String, messageCallback: (MessageInChat) -> Unit) {
        messagesSocket.connect(idChat, messageCallback)
    }

    fun disconnectSocket() {
        messagesSocket.disconnect()
    }

}