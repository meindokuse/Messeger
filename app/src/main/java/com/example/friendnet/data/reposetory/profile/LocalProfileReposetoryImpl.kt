package com.example.friendnet.data.reposetory.profile

import android.net.Uri
import com.example.friendnet.data.local.storage.FileManager
import com.example.friendnet.data.local.db.DataBase
import com.example.friendnet.data.maper.toProfileEntity
import com.example.friendnet.data.models.local.ProfileEntity
import com.example.friendnet.data.models.remote.ProfileDto
import com.example.friendnet.domain.reposetory.profile.LocalProfileReposetory
import com.example.friendnet.models.Event
import com.example.friendnet.models.ProfileInfo
import com.example.friendnet.domain.mapers.toProfileInfo

class LocalProfileReposetoryImpl(
    private val dataBase: DataBase,
    private val fileManager: FileManager
) : LocalProfileReposetory {

    private val profileDataBase = dataBase.profileDao
    override suspend fun getUserInfoLocal(): ProfileInfo {
        return profileDataBase.getUserProfileInfo()?.toProfileInfo() ?:
        ProfileInfo(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        )
    }

    override suspend fun addUser(profileInfo: ProfileEntity) {

        profileDataBase.createProfileInfo(profileInfo)
    }

    override suspend fun updateUserInfo(updateUserInfo: ProfileDto, avatar: Uri?) {
        if (avatar != null) fileManager.updateImageInInternalStorage(avatar, updateUserInfo.avatar)
        profileDataBase.updateProfileInfo(updateUserInfo.toProfileEntity())
    }

    override suspend fun addEvent(event: Event) {
//        localReposetoryHelper.addEventForRcView(event)
    }

    override fun initEvents(): List<Event> {
//        return localReposetoryHelper.getAllEvents()
        return emptyList()
    }

    override fun deleteEvent(event: Event) {
//        localReposetoryHelper.deleteEvent(event)
//        if (event.type == 2) fileManager.deleteFileFromInternalStorage(event.desc)
    }

    override fun startRecordAudio(): String {
        return fileManager.startRecordAudio().toString()
    }

    override fun stopRecordAudio() {
        fileManager.stopAudioRecord()
    }

    fun deleteAudio(fileName: String) {
        fileManager.deleteFileFromInternalStorage(fileName)
    }


}