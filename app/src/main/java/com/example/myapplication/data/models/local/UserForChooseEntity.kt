package com.example.myapplication.data.models.local

import androidx.room.Entity


@Entity
data class UserForChooseEntity(
    val id : String,
    val foto:String,
    val nickname:String
)
