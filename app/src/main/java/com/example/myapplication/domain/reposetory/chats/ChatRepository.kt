package com.example.myapplication.domain.reposetory.chats

import com.example.myapplication.data.models.remote.ChatDto
import com.example.myapplication.models.UserForChoose
import com.example.myapplication.util.api.DataForCreateChat

interface ChatRepository {

    suspend fun getAllChats(userId:String,page:Int,pageSize:Int):List<ChatDto>
    suspend fun createNewChat(dataForCreateChat: DataForCreateChat):ChatDto?

    suspend fun getUserForCreateChat(userId: String):List<UserForChoose>?

    suspend fun deleteChats(chatsId:List<String>):Boolean

}