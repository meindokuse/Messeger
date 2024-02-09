package com.example.myapplication.domain.reposetory.local

import android.net.Uri
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.models.UpdateUserInfo

interface LocalProfileReposetory{

    // для инициализации данных ,пока загружются актульные с remoteRepos
     fun getUserInfoLocal():ProfileInfo

     fun addUser(profileInfo: ProfileInfo)
     fun updateUserInfo(updateUserInfo: UpdateUserInfo, avatar: Uri?)
}