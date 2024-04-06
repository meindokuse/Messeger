package com.example.myapplication.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.models.local.RemoteKeysChat


@Dao
interface ChatRemoteKeysDao {


    @Query("SELECT * FROM remotekeyschat WHERE id =:id")
    suspend fun getRemoteKeys(id:String): RemoteKeysChat

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllRemoteKeys(remoteKeys: List<RemoteKeysChat>)
    @Query("DELETE FROM remotekeyschat")
    suspend fun deleteAllRemoteKeys()
}