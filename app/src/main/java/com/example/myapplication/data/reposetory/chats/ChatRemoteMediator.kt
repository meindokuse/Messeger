package com.example.myapplication.data.reposetory.chats

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.myapplication.data.local.db.DataBase
import com.example.myapplication.data.maper.toChatEntity
import com.example.myapplication.data.models.local.ChatEntity
import com.example.myapplication.data.models.local.RemoteKeysChat
import com.example.myapplication.domain.reposetory.chats.ChatRepository


@OptIn(ExperimentalPagingApi::class)
class ChatRemoteMediator(
    private val dataBase: DataBase,
    private val chatsRepository: ChatRepository,
    private val userId: String
) : RemoteMediator<Int, ChatEntity>() {

    private val chatDao = dataBase.chatDao
    private val chatKeysDao = dataBase.chatKeysDao
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ChatEntity>
    ): MediatorResult {

        return try {
            val loadkey = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: 1
                }

                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKeys?.prevKey
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    prevPage
                }

                LoadType.APPEND -> {
                    val remoteKeys = getLastRemoteKey(state)
                    val nextPage = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    nextPage
                }
            }

            val response = chatsRepository.getAllChats(userId, loadkey, 20)

            val endOfPaginationReached = response.isEmpty()

            val prevPage = if (loadkey == 1) null else loadkey - 1
            val nextPage = if (endOfPaginationReached) null else loadkey + 1

            dataBase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    chatDao.clearAll()
                    chatKeysDao.deleteAllRemoteKeys()
                }
                val keys = response.map { chat ->
                    RemoteKeysChat(
                        id = chat.chat_id,
                        prevKey = prevPage,
                        nextKey = nextPage
                    )
                }
                chatKeysDao.addAllRemoteKeys(remoteKeys = keys)
                chatDao.upsertAll(chats = response.map { it.toChatEntity() })
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: Exception) {
            Log.d("MyLog", "ошибка получения чатов")
            MediatorResult.Error(e)
        }
    }


    private suspend fun getLastRemoteKey(state: PagingState<Int, ChatEntity>): RemoteKeysChat? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { chatEntity ->
                chatKeysDao.getRemoteKeys(id = chatEntity.chatId)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, ChatEntity>
    ): RemoteKeysChat? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { chatEntity ->
                chatKeysDao.getRemoteKeys(id = chatEntity.chatId)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ChatEntity>
    ): RemoteKeysChat? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.chatId?.let { id ->
                chatKeysDao.getRemoteKeys(id = id)
            }
        }
    }

}