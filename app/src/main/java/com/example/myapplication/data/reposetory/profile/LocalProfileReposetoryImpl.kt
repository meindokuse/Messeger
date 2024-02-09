package com.example.myapplication.data.reposetory

import android.net.Uri
import com.example.myapplication.data.source.local.FileManager
import com.example.myapplication.data.source.local.LocalReposetoryHelper
import com.example.myapplication.domain.reposetory.local.LocalProfileReposetory
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.models.UpdateUserInfo

class LocalProfileReposetoryImpl(
    private val localReposetoryHelper: LocalReposetoryHelper,
    private val fileManager: FileManager
) : LocalProfileReposetory {
    override  fun getUserInfoLocal(): ProfileInfo {
        return localReposetoryHelper.getAllInfo()
    }

    override  fun addUser(profileInfo: ProfileInfo) {
        localReposetoryHelper.addProfile(profileInfo)
    }

    override  fun updateUserInfo(updateUserInfo: UpdateUserInfo, avatar: Uri?) {
        if (avatar != null) fileManager.updateImageInInternalStorage(
            avatar,
            updateUserInfo.fileName
        )

        localReposetoryHelper.updateProfile(
            updateUserInfo.newFirstName,
            updateUserInfo.newSecondName,
        )
    }

}