package com.example.friendnet.data.local.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.friendnet.data.models.local.MessageEntity


@Dao
interface MessageDao {

    @Upsert
    suspend fun upsertAll(messages: List<MessageEntity>)

    @Query("SELECT * FROM messageentity")
    fun getLastMessages(): PagingSource<Int, MessageEntity>

    @Query("DELETE FROM messageentity")
    suspend fun clearAll()
}