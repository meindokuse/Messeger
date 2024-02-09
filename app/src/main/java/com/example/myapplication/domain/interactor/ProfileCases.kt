package com.example.myapplication.domain.profile.interactor

import android.net.Uri
import androidx.core.net.toUri
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.models.UpdateUserInfo
import com.example.myapplication.models.UserDataResponse
import com.example.myapplication.data.remote.FirebaseStorageForImage
import com.example.myapplication.data.remote.RetrofitStorage
import com.example.myapplication.util.Constance

class ProfileCases() {
    suspend fun regNewUser(
        info: ArrayList<String>,
        avatar: Uri?,
        userId: String,
        fileName: String
    ): UserDataResponse {
        return if (avatar != null) {

            val firebaseStorageForImage = FirebaseStorageForImage(userId)
            val success = firebaseStorageForImage.loadData(avatar, fileName)
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

    suspend fun getUserInfo(userId: String): UserDataResponse {
        val response = RetrofitStorage.getUser(userId)
        return if (response.isSuccessful && response.body() != null) {
            UserDataResponse(Constance.SUCCESS, response.body()?.user)
        } else {
            UserDataResponse(Constance.NETWORK_ERROR, null)
        }
    }

    suspend fun updateUserInfo(
        updateUserInfo: UpdateUserInfo,
        avatar: Uri?,
        userId: String,
    ): Int {
        return if (avatar != null) {
            val firebaseStorageForImage = FirebaseStorageForImage(userId)

            val success = firebaseStorageForImage.loadData(avatar, updateUserInfo.newAvatarLink)
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

    suspend fun getLink(userId: String, fileName: String): Uri {
        val firebaseStorageForImage = FirebaseStorageForImage(userId)
        return firebaseStorageForImage.getData(fileName) ?: "error".toUri()
    }


}