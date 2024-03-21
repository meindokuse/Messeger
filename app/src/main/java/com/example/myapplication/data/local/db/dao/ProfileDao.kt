package com.example.myapplication.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.models.local.ProfileEntity


@Dao
interface ProfileDao {

    @Insert
    suspend fun createProfileInfo(profileInfo: ProfileEntity)

    @Update
    suspend fun updateProfileInfo(profileInfo: ProfileEntity)

    @Query("SELECT * FROM profileentity")
    suspend fun getUserProfileInfo():ProfileEntity
}