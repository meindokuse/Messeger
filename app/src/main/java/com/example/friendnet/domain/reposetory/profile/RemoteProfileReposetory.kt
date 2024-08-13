package com.example.friendnet.domain.reposetory.profile

import android.net.Uri
import com.example.friendnet.data.models.remote.ProfileDto
import com.example.friendnet.domain.models.UserDataResponse
import com.example.friendnet.util.api.NetworkApi

interface RemoteProfileReposetory {

    suspend fun regNewUser(
        info: ArrayList<String>,
        userId: String,
        fileName: String
    ): UserDataResponse
    suspend fun getUserInfo(userId: String): UserDataResponse
    suspend fun updateUserInfo(
        updateUserInfo: ProfileDto,
        avatar: Uri?,
    ): Int

    suspend fun loginUser(email:String,password:String): NetworkApi.LoginResponse?

    suspend fun getUri(userId: String,fileName: String):Uri?
}