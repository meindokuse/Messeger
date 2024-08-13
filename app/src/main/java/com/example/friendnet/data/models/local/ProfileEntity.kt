package com.example.friendnet.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class ProfileEntity(

    @PrimaryKey
    val userId:String,

    val firstname:String,
    val secondname:String,
    val school:String,
    val city:String,
    val age:String,
    val targetClass:String,
    val avatar:String,
    val email:String,
    val password:String
)
