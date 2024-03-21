package com.example.myapplication.data.local.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.myapplication.data.models.local.ChatEntity


@Dao
interface ChatDao {

    @Upsert
    suspend fun upsertAll(chats: List<ChatEntity>)

    @Query("SELECT * FROM chatentity")
    suspend fun getLastChats(): PagingSource<Int, ChatEntity>

    @Query("DELETE FROM chatentity")
    suspend fun clearAll()
}