package com.example.myapplication.domain.interactor

import android.net.Uri
import androidx.core.net.toUri
import com.example.myapplication.models.UpdateUserInfo
import com.example.myapplication.domain.models.UserDataResponse
import com.example.myapplication.data.remote.FirebaseStorage
import com.example.myapplication.domain.reposetory.profile.LocalProfileReposetory
import com.example.myapplication.domain.reposetory.profile.RemoteProfileReposetory
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.util.Constance

//class ProfileCases(
//    private val remoteProfileReposetory: RemoteProfileReposetory,
//    private val localProfileReposetory: LocalProfileReposetory
//) {
//    suspend fun regNewUser(
//        info: ArrayList<String>,
//        avatar: Uri?,
//        userId: String,
//        fileName: String
//    ): UserDataResponse {
//        val userResponse = remoteProfileReposetory.regNewUser(info, avatar, userId, fileName)
//        if (userResponse.code == Constance.SUCCESS && userResponse.data != null) localProfileReposetory.addUser(userResponse.data)
//        return userResponse
//    }
//
//    suspend fun getUserInfo(userId: String): UserDataResponse {
//        return remoteProfileReposetory.getUserInfo(userId)
//    }
//
//    suspend fun updateUserInfo(
//        updateUserInfo: UpdateUserInfo,
//        avatar: Uri?,
//        userId: String,
//    ): Int {
//        val code = remoteProfileReposetory.updateUserInfo(updateUserInfo, avatar, userId)
//        if(code == Constance.SUCCESS) localProfileReposetory.updateUserInfo(updateUserInfo,avatar)
//        return code
//    }
//
//     fun initProfileData():ProfileInfo{
//       return localProfileReposetory.getUserInfoLocal()
//    }
//
//    // не является useCase - это всмоготельный метод для загрузки фото по ссылке
//    // используется здесь во избежании сквозных связей
//    suspend fun getLink(userId: String, fileName: String): Uri {
//        val firebaseStorageForImage = FirebaseStorage(userId)
//        return firebaseStorageForImage.getData(fileName) ?: "error".toUri()
//    }
//
//
//}