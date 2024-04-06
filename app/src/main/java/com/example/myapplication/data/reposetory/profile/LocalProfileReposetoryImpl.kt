package com.example.myapplication.data.reposetory.profile

import android.net.Uri
import com.example.myapplication.data.local.storage.FileManager
import com.example.myapplication.data.local.db.DataBase
import com.example.myapplication.data.models.local.ProfileEntity
import com.example.myapplication.domain.reposetory.profile.LocalProfileReposetory
import com.example.myapplication.models.Event
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.models.UpdateUserInfo
import com.example.myapplication.domain.mapers.toProfileInfo

class LocalProfileReposetoryImpl(
    private val dataBase: DataBase,
    private val fileManager: FileManager
) : LocalProfileReposetory {

    private val profileDataBase = dataBase.profileDao
    override suspend fun getUserInfoLocal(): ProfileInfo {
        return profileDataBase.getUserProfileInfo().toProfileInfo()
    }

    override suspend fun addUser(profileInfo: ProfileEntity) {

        profileDataBase.createProfileInfo(profileInfo)
    }

    override suspend fun updateUserInfo(updateUserInfo: UpdateUserInfo, avatar: Uri?) {
        if (avatar != null) fileManager.updateImageInInternalStorage(avatar, updateUserInfo.fileName)
        profileDataBase.updateProfileInfo(updateUserInfo.profileEntity)
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