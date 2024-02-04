package com.example.myapplication.profile.domain

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.shared.Constance
import com.example.myapplication.shared.FileManager
import com.example.myapplication.R
import com.example.myapplication.models.Event
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.shared.AudioRecorder
import com.example.myapplication.models.UpdateUserInfo
import com.example.myapplication.reposetory.LocalReposetoryHelper
import com.example.myapplication.reposetory.RemoteReposetory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.Exception

open class ProfileViewModel(
    private val localReposetoryHelper: LocalReposetoryHelper,
    profileId: Long,
    application: Application
) : AndroidViewModel(application) {


    val userProfile: LiveData<ProfileInfo> = MutableLiveData()


    private fun updateUserProfile() {
        Log.d("MyLog", "Инит")
        Log.d("MyLog", "${localReposetoryHelper.getAllInfo()}")
        val profileInfo = localReposetoryHelper.getAllInfo()
        (userProfile as MutableLiveData).value = profileInfo

    }

    suspend fun syncUser(id: String): Int {
        return try {
            val profileInfo = RemoteReposetory.getUser(id)
            if (profileInfo != null) {
                withContext(Dispatchers.Main) {
                    (userProfile as MutableLiveData).value = profileInfo
                }
                localReposetoryHelper.updateProfile(
                    profileInfo.user_id,
                    profileInfo.firstname,
                    profileInfo.secondname,
                    profileInfo.avatar
                )
                1
            } else 0
        } catch (e: Exception) {
            Log.d("MyLog", e.message.toString())
            0
        }
    }

    suspend fun addUser(context: Context, info: ArrayList<String>, avatar: Bitmap?): Int =
        withContext(Dispatchers.IO) {
            try {
                val id = UUID.randomUUID().toString().reversed()
                val uniqueKey = UUID.randomUUID().toString()
                val fotoForAvatar =
                    avatar?.let { saveImage(it, uniqueKey) } ?: saveDefaultImage(context, uniqueKey)

                val profileInfo = ProfileInfo(
                    id,
                    info[0],
                    info[1],
                    info[2],
                    info[3],
                    info[4],
                    info[5],
                    fotoForAvatar,
                    info[6],
                    info[7]
                )

                val response = RemoteReposetory.regUser(profileInfo)
                if (response.isSuccessful) {
                    val sharedPreferences = context.getSharedPreferences(
                        Constance.KEY_USER_PREFERENCES,
                        Context.MODE_PRIVATE
                    )
                    sharedPreferences.edit().putString(Constance.KEY_USER_ID, id).apply()
                    localReposetoryHelper.addProfile(profileInfo)
                    withContext(Dispatchers.Main){
                        updateUserProfile()
                    }
                    200
                } else {
                    deleteFile(uniqueKey)
                    400
                }
            } catch (e: Exception) {
                Log.e("MyLog", "Ошибка при добавлении пользователя: ${e.message}")
                400
            }
        }

    private fun saveDefaultImage(context: Context, uniqueKey: String): String {
        val defaultFoto = BitmapFactory.decodeResource(context.resources, R.drawable.profile_foro)
        return saveImage(defaultFoto, uniqueKey)
    }

    fun uppdateProfile(FirstName: String, SecondName: String, avatar: Bitmap?) {
        val user = userProfile.value!!

        viewModelScope.launch(Dispatchers.IO) {
            if (avatar != null) {
                val newPath = UUID.randomUUID().toString()
                try {
                    val oldPath = user.avatar // Получаем текущий путь к файлу

                    val targetPathForAvatar = saveImage(avatar,newPath)
                    val updateUserInfo = UpdateUserInfo(FirstName, SecondName, targetPathForAvatar)
                    val response = RemoteReposetory.updateUser(user.user_id, updateUserInfo)

                    if (response.isSuccessful) {
                        Log.d("MyLog", targetPathForAvatar)
                        localReposetoryHelper.updateProfile(
                            user.user_id,
                            FirstName,
                            SecondName,
                            targetPathForAvatar
                        )
                        withContext(Dispatchers.Main) {
                            updateUserProfile()
                        }
                        deleteFile(oldPath)
                    } else {
                        deleteFile(targetPathForAvatar)
                    }
                } catch (e: Exception) {
                    Log.d("MyLog", "Ошибка updateProfile ${e.message}")
                }
            } else {
                try {
                    val updateUserInfo = UpdateUserInfo(FirstName, SecondName, user.avatar)
                    val response = RemoteReposetory.updateUser(user.user_id, updateUserInfo)
                    if (response.isSuccessful) {
                        localReposetoryHelper.updateProfile(
                            user.user_id,
                            FirstName,
                            SecondName,
                            user.avatar
                        )
                        withContext(Dispatchers.Main) {
                            updateUserProfile()
                        }
                    }
                } catch (e: Exception) {
                    Log.d("MyLog", "Ошибка updateProfile ${e.message}")
                }
            }
        }
    }

    //makes for RcView ( events )

    val userEvents: LiveData<List<Event>> = MutableLiveData()
    val UserEventRightNow: MutableLiveData<Event> = MutableLiveData()


    fun addEventToReposetory(event: Event) {
        localReposetoryHelper.addEventForRcView(event)
        updateUserEventsList()
        UserEventRightNow.value = event
    }

    private fun updateUserEventsList() {
        Log.d("MyLog", "Лист выгружен")
        (userEvents as MutableLiveData).value = localReposetoryHelper.getAllEvents()
        Log.d("MyLog", "${userEvents.value}")
    }

    fun DeleteEvent(event: Event) {
        localReposetoryHelper.deleteAll(event)
        if (event.type == 2) {
            deleteFile(event.desc)
        }
        updateUserEventsList()
    }

    // FOR SOUND EVENTS (POSTS)

    private val audioRecorder = AudioRecorder(application)

    val isRecording: MutableLiveData<Boolean> = MutableLiveData()


    fun startRecording(): String {
        return audioRecorder.startRecording()
    }

    fun stopRecording() {
        audioRecorder.stopRecording()
    }

    // for image in profile and soundFormat(posts)


    private val fileManager = FileManager(application)

    private fun saveImage(bitmap: Bitmap, fileName: String): String {
        return fileManager.saveImageToInternalStorage(bitmap, fileName)
    }

    private fun updateImageInInternalStorage(
        newBitmap: Bitmap,
        oldName: String,
        fileName: String
    ): String {
        Log.d("MyLog","updateImageInInternalStorage")
        return fileManager.updateImageInInternalStorage(newBitmap, oldName, fileName)
    }

    fun deleteFile(fileName: String) {
        fileManager.deleteImageFromInternalStorage(fileName)
    }

    init {
        updateUserProfile()
        updateUserEventsList()
        isRecording.value = false
    }


    //NETWORK METHODS
}