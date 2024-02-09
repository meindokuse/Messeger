package com.example.myapplication.domain.reposetory.profile

import android.net.Uri
import com.example.myapplication.models.UpdateUserInfo
import com.example.myapplication.domain.models.UserDataResponse

interface RemoteProfileReposetory {

    suspend fun regNewUser(
        info: ArrayList<String>,
        avatar: Uri?,
        userId: String,
        fileName: String
    ): UserDataResponse
    suspend fun getUserInfo(userId: String): UserDataResponse
    suspend fun updateUserInfo(
        updateUserInfo: UpdateUserInfo,
        avatar: Uri?,
        userId: String,
    ): Int

    suspend fun getUri(userId: String,fileName: String):Uri?
}