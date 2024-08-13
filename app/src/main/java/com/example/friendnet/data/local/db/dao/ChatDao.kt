package com.example.friendnet.data.local.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.friendnet.data.models.local.ChatEntity


@Dao
interface ChatDao {

    @Upsert
    suspend fun upsertAll(chats: List<ChatEntity>)

    @Query("SELECT * FROM chatentity")
    fun getLastChats(): PagingSource<Int, ChatEntity>

    @Query("DELETE FROM chatentity")
    suspend fun clearAll()

    @Query("DELETE FROM chatentity WHERE chatId = :id ")
    fun deleteChat(id:String)
}