package com.example.myapplication.data.reposetory.profile

import android.net.Uri
import com.example.myapplication.data.remote.FirebaseStorage
import com.example.myapplication.data.remote.RetrofitStorage
import com.example.myapplication.domain.reposetory.profile.RemoteProfileReposetory
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.models.UpdateUserInfo
import com.example.myapplication.domain.models.UserDataResponse
import com.example.myapplication.util.Constance


class RemoteUserReposImpl(
    private val firebaseStorage: FirebaseStorage,
) : RemoteProfileReposetory {
    override suspend fun regNewUser(
        info: ArrayList<String>,
        avatar: Uri?,
        userId: String,
        fileName: String
    ): UserDataResponse {
        return if (avatar != null) {

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
            val success = firebaseStorage.loadData(userId,avatar, fileName)
            val response = RetrofitStorage.regUser(profileInfo)

            if (success && response.isSuccessful) {

                UserDataResponse(Constance.SUCCESS, profileInfo)

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
            val success = firebaseStorage.loadData(userId,avatar, updateUserInfo.fileName)
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

    override suspend fun getUri(userId: String,fileName: String):Uri?{
       return firebaseStorage.getData(userId,fileName)
    }


}