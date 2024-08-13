package com.example.friendnet.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class UserForChooseEntity(
    @PrimaryKey
    val id : String,
    val foto:String,
    val firstName:String,
    val secondName:String
)
