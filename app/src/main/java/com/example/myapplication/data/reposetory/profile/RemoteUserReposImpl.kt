package com.example.myapplication.data.reposetory.profile

import android.net.Uri
import android.util.Log
import com.example.myapplication.data.maper.toProfileEntity
import com.example.myapplication.data.models.remote.LoginBody
import com.example.myapplication.data.models.remote.ProfileDto
import com.example.myapplication.data.remote.FirebaseStorage
import com.example.myapplication.domain.reposetory.profile.RemoteProfileReposetory
import com.example.myapplication.models.UpdateUserInfo
import com.example.myapplication.domain.models.UserDataResponse
import com.example.myapplication.util.Constance
import com.example.myapplication.util.api.NetworkApi


class RemoteUserReposImpl(
    private val userApi:NetworkApi,
    private val firebaseStorage: FirebaseStorage,
) : RemoteProfileReposetory {
    override suspend fun regNewUser(
        info: ArrayList<String>,
        userId: String,
        fileName: String
    ): UserDataResponse {

        val profileInfo = ProfileDto(
            userId,
            info[0],
            info[1],
            info[2],
            info[3],
            info[4],
            info[5],
            fileName,
            info[6],
            info[7]
        )
        val response = userApi.postUser(profileInfo)
        return if (response.isSuccessful) {
            UserDataResponse(Constance.SUCCESS, profileInfo.toProfileEntity())
        } else {
            UserDataResponse(Constance.NETWORK_ERROR, null)
        }
    }

    override suspend fun getUserInfo(userId: String): UserDataResponse {
        val response = userApi.getInfoUser(userId)
        return if (response.isSuccessful && response.body() != null) {
            Log.d("MyLog", "for profile get response.isSuccessful")
            UserDataResponse(Constance.SUCCESS, response.body()?.user?.toProfileEntity())
        } else {
            Log.d("MyLog", "for profile get NETWORK_ERROR")
            UserDataResponse(Constance.NETWORK_ERROR, null)

        }

    }

    override suspend fun updateUserInfo(
        updateUserInfo: UpdateUserInfo,
        avatar: Uri?,
        userId: String
    ): Int {
        return if (avatar != null) {
            Log.d("MyLog", "non null avatar $avatar")
            val success = firebaseStorage.loadData(userId, avatar, updateUserInfo.fileName)
            Log.d("MyLog","$success")
            val response = userApi.updateUser(userId, updateUserInfo)

            if (success && response.isSuccessful) {
                Constance.SUCCESS
            } else Constance.NETWORK_ERROR
        } else {
            val response = userApi.updateUser(userId, updateUserInfo)
            if (response.isSuccessful) Constance.SUCCESS else Constance.NETWORK_ERROR
        }
    }

    override suspend fun loginUser(email: String, password: String): NetworkApi.LoginResponse? {
        val loginBody = LoginBody(email, password)
        val response = userApi.loginUser(loginBody)
        return response.body()
    }

    override suspend fun getUri(userId: String, fileName: String): Uri? {
        return firebaseStorage.getData(userId, fileName)
    }


}