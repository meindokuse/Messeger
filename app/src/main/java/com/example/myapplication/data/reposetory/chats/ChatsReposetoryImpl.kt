package com.example.myapplication.data.reposetory.chats

import android.util.Log
import com.example.myapplication.data.remote.FirebaseStorage
import com.example.myapplication.data.remote.RetrofitStorage
import com.example.myapplication.domain.reposetory.chats.RemoteChatsReposetory
import com.example.myapplication.models.ItemChat
import com.example.myapplication.models.UserForChoose
import com.example.myapplication.util.api.DataForCreateChat

class ChatsReposetoryImpl(
    private val firebaseStorage: FirebaseStorage,
) : RemoteChatsReposetory {
    override suspend fun getAllChats(userId: String,page:Int,pageSize:Int): List<ItemChat> {
        val readyChats = ArrayList<ItemChat>()
        RetrofitStorage.getAllChats(userId,page,pageSize)?.forEach { chat ->
            val anotherUserId = if (userId == chat.user_id_1) chat.user_id_2 else chat.user_id_1
            Log.d("MyLog","anotherUserId $anotherUserId")
            val uri = firebaseStorage.getData(anotherUserId, chat.avatar)
            uri?.let {
                readyChats.add(chat.copy(avatar = uri.toString()))
            }
        }
        return readyChats
    }

    override suspend fun createNewChat(dataForCreateChat: DataForCreateChat): ItemChat? {

        val anotherUserId = dataForCreateChat.chat.user_id_2

        val response = RetrofitStorage.createNewChat(dataForCreateChat)
        return if (response.isSuccessful) {
            val chat = dataForCreateChat.chat
            val uri = firebaseStorage.getData(anotherUserId, chat.avatar)
            uri?.let {
                chat.copy(avatar = uri.toString())
            }
        } else {
            null
        }
    }

    override suspend fun getUserForCreateChat(userId: String): List<UserForChoose> {
        val readyUsers = ArrayList<UserForChoose>()
        val users = RetrofitStorage.getUsersForCreateNewChat(userId)
        Log.d("MyLog", "qweewq $users")
        users?.forEach { user ->
            Log.d("MyLog", "getUserForCreateChat ${user.id} ${user.nickname}")
            val uri = firebaseStorage.getData(user.id, user.foto)
            uri?.let {
                readyUsers.add(user.copy(foto = it.toString()))
            }
        }

        return readyUsers
    }

}