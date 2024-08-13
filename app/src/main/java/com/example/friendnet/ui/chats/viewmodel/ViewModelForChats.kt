package com.example.friendnet.ui.chats.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.friendnet.data.local.db.DataBase
import com.example.friendnet.data.maper.toChatEntity
import com.example.friendnet.data.maper.toUserForChooseUI
import com.example.friendnet.data.models.remote.ChatDto
import com.example.friendnet.data.models.remote.MessageDto
import com.example.friendnet.data.reposetory.chats.ChatRemoteMediator
import com.example.friendnet.util.api.DataForCreateChat
import com.example.friendnet.models.ItemChat
import com.example.friendnet.models.UserForChoose
import com.example.friendnet.domain.reposetory.chats.ChatRepository
import com.example.friendnet.domain.mapers.toItemChat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
open class ViewModelForChats @Inject constructor(
    private val chatRepository: ChatRepository,
    private val dataBase: DataBase
) : ViewModel() {

    private val _usersForNewChat: MutableStateFlow<List<UserForChoose>> =
        MutableStateFlow(emptyList())
    val usersForNewChat: StateFlow<List<UserForChoose>> = _usersForNewChat

    private val _newChats: MutableStateFlow<List<ItemChat>> =
        MutableStateFlow(emptyList())
    val newChats: StateFlow<List<ItemChat>> = _newChats


    @OptIn(ExperimentalPagingApi::class)
    fun initChats(userId: String): Flow<PagingData<ItemChat>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { dataBase.chatDao.getLastChats() },
            remoteMediator = ChatRemoteMediator(
                dataBase,
                chatRepository,
                userId
            )
        ).flow.map { it -> it.map { it.toItemChat() } }.cachedIn(viewModelScope)
    }

    fun createNewChats(

        listWhoGetMes: List<UserForChoose>,
        text: String,
        userCreater: String
    ) {
        val newChatsList = ArrayList<ItemChat>()

        viewModelScope.launch(Dispatchers.IO) {
            for (user in listWhoGetMes) {

                val chatId = UUID.randomUUID().toString()
                val messageId = UUID.randomUUID().toString()
                val currentTime = System.currentTimeMillis()

                val messageInChat = MessageDto(
                    messageId,
                    userCreater,
                    chatId,
                    text,
                    currentTime,
                    1,
                    "0:00"
                )
                val nickname = user.firstName + ' ' + user.secondName
                val itemChat = ChatDto(
                    chatId,
                    userCreater,
                    user.id,
                    user.foto,
                    nickname,
                    text,
                    currentTime
                )
                val dataForCreateChat = DataForCreateChat(itemChat, messageInChat)
                val response = chatRepository.createNewChat(dataForCreateChat)?.toChatEntity()?.toItemChat()
                if (response != null) newChatsList.add(response)
            }
            _newChats.emit(newChatsList)
        }
    }

    suspend fun clearNewChats(){
        _newChats.emit(_newChats.value.toMutableList().apply {
            clear()
        })
    }


    suspend fun getUsersForNewChat(userId: String) {
        try {
            Log.d("MyLog", "getUsersForNewChat in viewModel $userId")
            val users = chatRepository.getUserForCreateChat(userId).map {
                it.toUserForChooseUI()
            }
            if (users.isNotEmpty()) {
                _usersForNewChat.emit(users)
            }

        } catch (e: Exception) {
            Log.d("MyLog", "ошибка при получении пользователей ${e.message}")
        }

    }

    suspend fun deleteChat(chatsForDelete: List<String>): Boolean =
        withContext(Dispatchers.IO) {
            chatRepository.deleteChats(chatsForDelete)
        }


}