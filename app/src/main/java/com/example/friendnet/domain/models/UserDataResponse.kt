package com.example.friendnet.domain.models

import com.example.friendnet.data.models.local.ProfileEntity

data class UserDataResponse(
    val code:Int,
    val data: ProfileEntity?
)
