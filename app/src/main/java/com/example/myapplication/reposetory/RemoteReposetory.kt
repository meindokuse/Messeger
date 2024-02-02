package com.example.myapplication.reposetory

import com.example.myapplication.api.DataForCreateChat
import com.example.myapplication.api.NetworkApi
import com.example.myapplication.api.RetrofitInstanse
import com.example.myapplication.models.MessageInChat
import com.example.myapplication.models.ItemChat
import com.example.myapplication.models.ProfileInfo
import retrofit2.Response

object RemoteReposetory {

    private val retrofit = RetrofitInstanse.api

    suspend fun regUser(user: ProfileInfo): Response<NetworkApi.ApiResponse> {
        return retrofit.postUser(user)
    }
    suspend fun getUser(idUser:String):ProfileInfo?{
        val response = retrofit.getInfoUser(idUser)
        return if (response.isSuccessful){
            response.body()?.user
        } else{
            null
        }
    }
    suspend fun createNewChat(dataForNewChat: DataForCreateChat): Response<NetworkApi.ApiResponse> {
        return retrofit.postChat(dataForNewChat)
    }
    suspend fun getAllChats(idUser:String):List<ItemChat>?{
        val response = retrofit.getAllChats(idUser)
        return if (response.isSuccessful){
            response.body()?.chats
        } else{
            null
        }
    }
    suspend fun createNewMessage(message: MessageInChat):Response<NetworkApi.ApiResponse>{
        return retrofit.postMessage(message)
    }
    suspend fun getAllMessage(chatId: String):List<MessageInChat>?{
        val response = retrofit.getAllMessage(chatId)
        return if (response.isSuccessful){
            response.body()?.messages
        } else{
            null
        }
    }

}