package com.example.myapplication.data.reposetory.message


import com.example.myapplication.data.remote.RetrofitStorage
import com.example.myapplication.domain.reposetory.message.RemoteMessagesReposetory
import com.example.myapplication.models.MessageInChat

import com.example.myapplication.util.Constance


class RemoteMessagesReposetoryImpl(

):RemoteMessagesReposetory {
    override suspend fun getListMessages(chatId: String): List<MessageInChat>? {
        return RetrofitStorage.getAllMessage(chatId)
    }

    override suspend fun createNewMessage(message: MessageInChat): Int {
        val response = RetrofitStorage.createNewMessage(message)
        return if (response.isSuccessful) Constance.SUCCESS else Constance.NETWORK_ERROR
    }
}