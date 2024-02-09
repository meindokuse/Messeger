package com.example.myapplication.ui.chats.domain

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.util.FileManager
import com.example.myapplication.api.DataForCreateChat
import com.example.myapplication.models.ItemChat
import com.example.myapplication.models.MessageInChat
import com.example.myapplication.models.UserForChoose
import com.example.myapplication.domain.LocalReposetoryHelper
import com.example.myapplication.data.remote.RemoteReposetory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

open class ViewModelForChats(
    private val localReposetoryHelper: LocalReposetoryHelper,
    @get:JvmName("getApplicationNonNull") val application: Application
) : AndroidViewModel(application) {

    private val _listOfChats: MutableStateFlow<List<ItemChat>> = MutableStateFlow(emptyList())
    val listOfChats: StateFlow<List<ItemChat>> = _listOfChats

    private val _listMessages: MutableStateFlow<List<MessageInChat>> = MutableStateFlow(emptyList())
    val listMessages: StateFlow<List<MessageInChat>> = _listMessages

    private val _usersForNewChat: MutableStateFlow<List<UserForChoose>> = MutableStateFlow(emptyList())
    val usersForNewChat: StateFlow<List<UserForChoose>> = _usersForNewChat

    private val fileManager = FileManager(application)

    private suspend fun initChatList() {
        Log.d("MyLog", "Обновление модели чатов")
        val savedChats = localReposetoryHelper.GetAllChats()
        _listOfChats.emit(savedChats)

    }

    suspend fun syncChats(userId:String){
        val chats = RemoteReposetory.getAllChats(userId)
        if (chats != null) _listOfChats.emit(chats)
    }

    fun AddNewChat(context: Context, user: UserForChoose, text: String, userCreater: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val bitmapFoto = BitmapFactory.decodeResource(
                context.resources,
                user.foto
            )
            val chatId = UUID.randomUUID().toString()
            val messageId = UUID.randomUUID().toString()
            val currentTime = System.currentTimeMillis()
            val fotoInChat = fileManager.saveImageToInternalStorage(bitmapFoto, chatId)

            val messageInChat = MessageInChat(
                messageId,
                userCreater,
                chatId,
                text,
                currentTime
            )
            val itemChat = ItemChat(
                chatId,
                userCreater,
                user.id,
                fotoInChat,
                user.nickname,
                text,
                currentTime
            )
            val dataForCreateChat = DataForCreateChat(itemChat, messageInChat)
            val response = RemoteReposetory.createNewChat(dataForCreateChat)

            if (response.isSuccessful) {
                localReposetoryHelper.addChat(itemChat)
                _listOfChats.emit(_listOfChats.value.toMutableList().apply { add(itemChat) })
            } else Log.d("MyLog", "${response.body()} ошибка сети ")
        }
    }

    fun AddChats(
        context: Context,
        listWhoGetMes: List<UserForChoose>,
        text: String,
        userCreater: String
    ) {
        val NewChatsList = ArrayList<ItemChat>()

        viewModelScope.launch(Dispatchers.IO) {
            for (user in listWhoGetMes) {
                val bitmapFoto = BitmapFactory.decodeResource(
                    context.resources,
                    user.foto
                )
                val chatId = UUID.randomUUID().toString()
                val messageId = UUID.randomUUID().toString()
                val currentTime = System.currentTimeMillis()
                val fotoInChat = fileManager.saveImageToInternalStorage(bitmapFoto, chatId)

                val messageInChat = MessageInChat(
                    messageId,
                    userCreater,
                    chatId,
                    text,
                    currentTime
                )
                val itemChat = ItemChat(
                    chatId,
                    userCreater,
                    user.id,
                    fotoInChat,
                    user.nickname,
                    text,
                    currentTime
                )
                val dataForCreateChat = DataForCreateChat(itemChat, messageInChat)
                val response = RemoteReposetory.createNewChat(dataForCreateChat)
                if (response.isSuccessful) {
                    NewChatsList.add(itemChat)
                }
            }
            localReposetoryHelper.AddNewChats(NewChatsList)
            _listOfChats.emit(_listOfChats.value.toMutableList().apply { addAll(NewChatsList) })
        }
    }

    fun getUsersForNewChat(){

    }

    fun sendMessage(message: MessageInChat) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = RemoteReposetory.createNewMessage(message)
            if (response.isSuccessful) {
                _listMessages.emit(
                    _listMessages.value.toMutableList().apply { add(message) })
            }
        }
    }

    suspend fun getAllMessages(idChat: String) {
        val list = RemoteReposetory.getAllMessage(idChat)
        if (list != null) _listMessages.emit(_listMessages.value.toMutableList().apply { addAll(list) })
    }

    fun deleteChat(chats: List<ItemChat>) {
        viewModelScope.launch(Dispatchers.IO) {
            localReposetoryHelper.ChatDelete(chats)
            _listOfChats.emit(_listOfChats.value.toMutableList().apply { removeAll(chats) })
            chats.forEach {
                Log.d("MyLog", it.chat_id)
                fileManager.deleteImageFromInternalStorage(it.chat_id)
            }
        }

    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            initChatList()
        }
    }
}