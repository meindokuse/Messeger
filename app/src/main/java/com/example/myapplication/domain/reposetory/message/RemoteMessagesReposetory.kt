package com.example.myapplication.domain.reposetory.message

import com.example.myapplication.data.models.remote.MessageDto
import com.example.myapplication.models.MessageInChat

interface RemoteMessagesReposetory {

   suspend fun getListMessages(chatId:String,page:Int,pageSize:Int):List<MessageDto>
   suspend fun createNewMessage(idChat: String,message:MessageInChat):Boolean
}