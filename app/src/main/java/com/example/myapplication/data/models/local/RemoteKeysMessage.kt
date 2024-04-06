package com.example.myapplication.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class RemoteKeysMessage(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)
