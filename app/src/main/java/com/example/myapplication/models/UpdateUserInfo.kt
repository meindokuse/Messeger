package com.example.myapplication.models

import com.example.myapplication.data.models.local.ProfileEntity

data class UpdateUserInfo(
    val profileEntity: ProfileEntity,
    val fileName:String
)
