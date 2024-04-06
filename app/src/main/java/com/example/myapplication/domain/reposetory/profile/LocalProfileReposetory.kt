package com.example.myapplication.domain.reposetory.profile

import android.net.Uri
import com.example.myapplication.data.models.local.ProfileEntity
import com.example.myapplication.models.Event
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.models.UpdateUserInfo

interface LocalProfileReposetory {

    // для инициализации данных ,пока загружются актульные с remoteRepos
    suspend fun getUserInfoLocal(): ProfileInfo

    suspend fun addUser(profileInfo: ProfileEntity)
    suspend fun updateUserInfo(updateUserInfo: UpdateUserInfo, avatar: Uri?)

    suspend fun addEvent(event: Event)

    fun initEvents():List<Event>

    fun deleteEvent(event: Event)

    fun startRecordAudio():String

    fun stopRecordAudio()


}