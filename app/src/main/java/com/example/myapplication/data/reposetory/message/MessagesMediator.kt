package com.example.myapplication.data.reposetory.message

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.myapplication.data.local.db.DataBase
import com.example.myapplication.data.maper.toMessageEntity
import com.example.myapplication.data.models.local.MessageEntity
import com.example.myapplication.data.models.local.RemoteKeysMessage

@OptIn(ExperimentalPagingApi::class)
class MessagesMediator(
    private val dataBase: DataBase,
    private val chatsRepository: RemoteMessagesReposetoryImpl,
    private val chatId: String
) : RemoteMediator<Int, MessageEntity>() {

    private val messagesDao = dataBase.messageDao
    private val messageRemoteKeysDao = dataBase.messageKeysDao
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MessageEntity>
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

            val response = chatsRepository.getListMessages(chatId, loadkey, 20).sortedByDescending {
                it.time
            }

            val endOfPaginationReached = response.isEmpty()

            val prevPage = if (loadkey == 1) null else loadkey - 1
            val nextPage = if (endOfPaginationReached) null else loadkey + 1

            dataBase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    messagesDao.clearAll()
                    messageRemoteKeysDao.deleteAllRemoteKeys()
                }
                val keys = response.map { message ->
                    RemoteKeysMessage(
                        id = message.message_id,
                        prevKey = prevPage,
                        nextKey = nextPage
                    )
                }
                messageRemoteKeysDao.addAllRemoteKeys(remoteKeys = keys)
                messagesDao.upsertAll(messages = response.map { it.toMessageEntity() })
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: Exception) {
            Log.d("MyLog", "ошибка получения чатов")
            MediatorResult.Error(e)
        }
    }


    private suspend fun getLastRemoteKey(state: PagingState<Int, MessageEntity>): RemoteKeysMessage? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { chatEntity ->
                messageRemoteKeysDao.getRemoteKeys(id = chatEntity.messageId)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, MessageEntity>
    ): RemoteKeysMessage? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { chatEntity ->
                messageRemoteKeysDao.getRemoteKeys(id = chatEntity.messageId)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, MessageEntity>
    ): RemoteKeysMessage? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.messageId?.let { id ->
                messageRemoteKeysDao.getRemoteKeys(id = id)
            }
        }
    }

}