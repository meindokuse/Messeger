package com.example.friendnet.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.friendnet.data.models.local.RemoteKeysMessage


@Dao
interface MessageRemoteKeysDao {

    @Query("SELECT * FROM remotekeysmessage WHERE id =:id")
    suspend fun getRemoteKeys(id:String): RemoteKeysMessage

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllRemoteKeys(remoteKeys: List<RemoteKeysMessage>)
    @Query("DELETE FROM remotekeysmessage")
    suspend fun deleteAllRemoteKeys()
}