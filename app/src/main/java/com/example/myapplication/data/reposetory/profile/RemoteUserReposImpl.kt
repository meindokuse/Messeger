package com.example.myapplication.data.reposetory

import android.net.Uri
import com.example.myapplication.data.remote.FirebaseStorage
import com.example.myapplication.data.remote.RetrofitStorage
import com.example.myapplication.data.source.local.LocalReposetoryHelper
import com.example.myapplication.domain.reposetory.remote.RemoteProfileReposetory
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.models.UpdateUserInfo
import com.example.myapplication.models.UserDataResponse
import com.example.myapplication.util.Constance


class RemoteUserReposImpl(
    private val firebaseStorage: FirebaseStorage,
    private val localReposetoryHelper: LocalReposetoryHelper
): RemoteProfileReposetory {
    override suspend fun regNewUser(
        info: ArrayList<String>,
        avatar: Uri?,
        userId: String,
        fileName: String
    ): UserDataResponse {
        return if (avatar != null) {

            val success = firebaseStorage.loadData(avatar, fileName)
            if (success) {
                val profileInfo = ProfileInfo(
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
                val response = RetrofitStorage.regUser(profileInfo)
                if (response.isSuccessful) {
                    UserDataResponse(Constance.SUCCESS, profileInfo)
                } else {
                    UserDataResponse(Constance.NETWORK_ERROR, null)
                }
            } else {
                UserDataResponse(Constance.NETWORK_ERROR, null)
            }
        } else {
            val profileInfo = ProfileInfo(
                userId,
                info[0],
                info[1],
                info[2],
                info[3],
                info[4],
                info[5],
                "resource_default_foto",
                info[6],
                info[7]
            )
            val response = RetrofitStorage.regUser(profileInfo)
            if (response.isSuccessful) {
                UserDataResponse(Constance.SUCCESS, profileInfo)
            } else {
                UserDataResponse(Constance.NETWORK_ERROR, null)
            }
        }
    }

    override suspend fun getUserInfo(userId: String): UserDataResponse {
        val response = RetrofitStorage.getUser(userId)
        return if (response.isSuccessful && response.body() != null) {
            UserDataResponse(Constance.SUCCESS, response.body()?.user)
        } else {
            UserDataResponse(Constance.NETWORK_ERROR, null)
        }

    }

    override suspend fun updateUserInfo(
        updateUserInfo: UpdateUserInfo,
        avatar: Uri?,
        userId: String
    ): Int {
        return if (avatar != null) {
            val success = firebaseStorage.loadData(avatar, updateUserInfo.fileName)
            val response = RetrofitStorage.updateUser(userId, updateUserInfo)

            if (success && response.isSuccessful) {
                Constance.SUCCESS
            } else Constance.NETWORK_ERROR
        } else {
            val response = RetrofitStorage.updateUser(userId, updateUserInfo)
            if (response.isSuccessful) Constance.SUCCESS
            else Constance.NETWORK_ERROR
        }
    }


}