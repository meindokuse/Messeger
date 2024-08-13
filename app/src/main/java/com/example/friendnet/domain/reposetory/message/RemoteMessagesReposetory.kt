package com.example.friendnet.domain.reposetory.message

import com.example.friendnet.data.models.remote.MessageDto
import com.example.friendnet.models.MessageInChat

interface RemoteMessagesReposetory {

   suspend fun getListMessages(chatId:String,page:Int,pageSize:Int):List<MessageDto>
   suspend fun createNewMessage(idChat: String,message:MessageInChat):Boolean
}