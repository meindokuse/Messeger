package com.example.myapplication.domain.reposetory.message

import com.example.myapplication.models.MessageInChat

interface RemoteMessagesReposetory {

   suspend fun getListMessages(chatId:String):List<MessageInChat>?
   suspend fun createNewMessage(idChat: String,message:MessageInChat):Boolean
}