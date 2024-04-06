package com.example.myapplication.domain.models

import com.example.myapplication.data.models.local.ProfileEntity
import com.example.myapplication.models.ProfileInfo

data class UserDataResponse(
    val code:Int,
    val data: ProfileEntity?
)
