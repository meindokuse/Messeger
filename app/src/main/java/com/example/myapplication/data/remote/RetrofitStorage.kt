package com.example.myapplication.data.remote

import android.util.Log
import com.example.myapplication.data.models.ListForDeleteChat
import com.example.myapplication.data.models.LoginBody
import com.example.myapplication.util.api.DataForCreateChat
import com.example.myapplication.util.api.NetworkApi
import com.example.myapplication.util.api.RetrofitInstanse
import com.example.myapplication.util.api.User
import com.example.myapplication.models.MessageInChat
import com.example.myapplication.models.ItemChat
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.models.UpdateUserInfo
import com.example.myapplication.models.UserForChoose
import retrofit2.Response

object RetrofitStorage {

    private val retrofit = RetrofitInstanse.api

    suspend fun regUser(user: ProfileInfo): Response<NetworkApi.ApiResponse> {
        return retrofit.postUser(user)
    }
    suspend fun getUser(idUser:String):Response<User>{
        return retrofit.getInfoUser(idUser)
    }

    suspend fun updateUser(userId:String,updateUserInfo: UpdateUserInfo):Response<NetworkApi.ApiResponse>{
        return retrofit.updateUser(userId,updateUserInfo)
    }

    suspend fun createNewChat(dataForNewChat: DataForCreateChat): Response<NetworkApi.ApiResponse> {
        return retrofit.postChat(dataForNewChat)
    }
    suspend fun getAllChats(idUser:String,page:Int,pageSize:Int):List<ItemChat>?{
        val response = retrofit.getAllChats(idUser,page,pageSize)
        return if (response.isSuccessful){
            Log.d("MyLog","Retrofit chats ${response.body()?.chats}")
            response.body()?.chats
        } else{
            null
        }
    }
    suspend fun getUsersForCreateNewChat(userId: String): List<UserForChoose>? {
        val response = retrofit.getUsersForNewChat(userId)
        return if (response.isSuccessful){
            Log.d("MyLog","getUsersForCreateNewChat isSuccessful ${response.body()?.usersForChoose}")
            response.body()?.usersForChoose
        } else{
            Log.d("MyLog","getUsersForCreateNewChat is null")
            null
        }
    }

    suspend fun createNewMessage(message: MessageInChat):Response<NetworkApi.ApiResponse>{
        return retrofit.postMessage(message)
    }
    suspend fun getAllMessage(chatId: String,page:Int,pageSize: Int):List<MessageInChat>?{
        val response = retrofit.getAllMessage(chatId,page,pageSize)
        return if (response.isSuccessful){
            response.body()?.messages
        } else{
            null
        }
    }

    suspend fun loginUser(loginBody: LoginBody):Response<NetworkApi.LoginResponse>{
        return retrofit.loginUser(loginBody)
    }

    suspend fun deleteChat(chatsId: List<String>):Boolean{
        val listForDeleteChat = ListForDeleteChat(chatsId)
        return retrofit.deleteChat(listForDeleteChat).isSuccessful
    }


}