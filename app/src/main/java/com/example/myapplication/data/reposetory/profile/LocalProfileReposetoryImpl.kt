package com.example.myapplication.data.reposetory.profile

import android.net.Uri
import com.example.myapplication.data.local.FileManager
import com.example.myapplication.data.local.LocalReposetoryHelper
import com.example.myapplication.domain.reposetory.profile.LocalProfileReposetory
import com.example.myapplication.models.Event
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
        if (avatar != null) fileManager.updateImageInInternalStorage(avatar, updateUserInfo.fileName)

        localReposetoryHelper.updateProfile(
            updateUserInfo.newFirstName,
            updateUserInfo.newSecondName,
        )
    }

    override fun addEvent(event: Event) {
        localReposetoryHelper.addEventForRcView(event)
    }

    override fun initEvents():List<Event> {
        return localReposetoryHelper.getAllEvents()
    }

    override fun deleteEvent(event: Event) {
        localReposetoryHelper.deleteEvent(event)
        if (event.type == 2) fileManager.deleteFileFromInternalStorage(event.desc)
    }

    override fun startRecordAudio(): String {
        return fileManager.startRecordAudio().toString()
    }

    override fun stopRecordAudio(){
        fileManager.stopAudioRecord()
    }

    fun deleteAudio(fileName:String){
        fileManager.deleteFileFromInternalStorage(fileName)
    }



}