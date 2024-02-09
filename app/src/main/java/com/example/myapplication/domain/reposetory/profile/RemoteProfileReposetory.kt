package com.example.myapplication.domain.reposetory.remote

import android.net.Uri
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.models.UpdateUserInfo
import com.example.myapplication.models.UserDataResponse
import com.example.myapplication.util.api.NetworkApi
import com.example.myapplication.util.api.User
import retrofit2.Response

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
}