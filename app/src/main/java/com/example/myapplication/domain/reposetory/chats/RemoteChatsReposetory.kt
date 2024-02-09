package com.example.myapplication.domain.reposetory.chats

import com.example.myapplication.models.ItemChat
import com.example.myapplication.models.UserForChoose
import com.example.myapplication.util.api.DataForCreateChat

interface RemoteChatsReposetory {

    suspend fun getAllChats(userId:String):List<ItemChat>?
    suspend fun createNewChat(dataForCreateChat: DataForCreateChat):ItemChat?

    suspend fun getUserForCreateChat(userId: String):List<UserForChoose>?

}