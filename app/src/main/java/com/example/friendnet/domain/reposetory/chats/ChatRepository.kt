package com.example.friendnet.domain.reposetory.chats

import com.example.friendnet.data.models.remote.ChatDto
import com.example.friendnet.data.models.remote.UserForChooseDto
import com.example.friendnet.models.UserForChoose
import com.example.friendnet.util.api.DataForCreateChat

interface ChatRepository {

    suspend fun getAllChats(userId:String,page:Int,pageSize:Int):List<ChatDto>
    suspend fun createNewChat(dataForCreateChat: DataForCreateChat):ChatDto?

    suspend fun getUserForCreateChat(userId: String):List<UserForChooseDto>

    suspend fun deleteChats(chatsId:List<String>):Boolean

}