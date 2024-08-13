package com.example.friendnet.domain.reposetory.profile

import android.net.Uri
import com.example.friendnet.data.models.local.ProfileEntity
import com.example.friendnet.data.models.remote.ProfileDto
import com.example.friendnet.models.Event
import com.example.friendnet.models.ProfileInfo

interface LocalProfileReposetory {

    // для инициализации данных ,пока загружются актульные с remoteRepos
    suspend fun getUserInfoLocal(): ProfileInfo

    suspend fun addUser(profileInfo: ProfileEntity)
    suspend fun updateUserInfo(updateUserInfo: ProfileDto, avatar: Uri?)

    suspend fun addEvent(event: Event)

    fun initEvents():List<Event>

    fun deleteEvent(event: Event)

    fun startRecordAudio():String

    fun stopRecordAudio()


}