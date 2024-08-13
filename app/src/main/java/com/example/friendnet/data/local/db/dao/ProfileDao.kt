package com.example.friendnet.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.friendnet.data.models.local.ProfileEntity


@Dao
interface ProfileDao {

    @Insert(entity = ProfileEntity::class)
    suspend fun createProfileInfo(profileInfo: ProfileEntity)

    @Update
    suspend fun updateProfileInfo(profileInfo: ProfileEntity)

    @Query("SELECT * FROM profileentity")
    suspend fun getUserProfileInfo():ProfileEntity?
}