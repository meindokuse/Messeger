package com.example.myapplication.ui.profile.domain

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.util.Constance
import com.example.myapplication.util.FileManager
import com.example.myapplication.R
import com.example.myapplication.models.Event
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.util.AudioRecorder
import com.example.myapplication.models.UpdateUserInfo
import com.example.myapplication.domain.LocalReposetoryHelper
import com.example.myapplication.domain.UserCaseImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.Exception

open class ProfileViewModel(
    private val localReposetoryHelper: LocalReposetoryHelper,
    application: Application,
) : AndroidViewModel(application) {


    private val _userProfile: MutableLiveData<ProfileInfo> = MutableLiveData()
    val userProfile: LiveData<ProfileInfo> get() = _userProfile

    private fun initUserInfoLocal() {
        Log.d("MyLog", "Инит")
        Log.d("MyLog", "${localReposetoryHelper.getAllInfo()}")
        val profileInfo = localReposetoryHelper.getAllInfo()
        _userProfile.postValue(profileInfo)
    }

    private val userCase = UserCaseImpl()


    suspend fun syncUser(id: String): Int {
        return try {
            val response = userCase.getUserInfo(id)
            if (response.user != null) {
                val user = response.user
                val fileName = user.avatar
                val link = userCase.getLink(id, fileName)
                withContext(Dispatchers.Main) {
                    _userProfile.postValue(user.copy(avatar = link.toString()))
                }
                response.code
            } else {
                response.code
            }
        } catch (e: Exception) {
            Log.d("MyLog", e.message.toString())
            Constance.NETWORK_ERROR
        }
    }

    suspend fun addUser(context: Context, info: ArrayList<String>, avatar: Uri?): Int =
        withContext(Dispatchers.IO) {
            try {

                val id = UUID.randomUUID().toString().reversed()
                val fileName = "avatar_${UUID.randomUUID()}.jpg"

                val response = userCase.regNewUser(info, avatar, id, fileName)
                if (response.user != null) {
                    val user = response.user
                    val sharedPreferences = context.getSharedPreferences(
                        Constance.KEY_USER_PREFERENCES,
                        Context.MODE_PRIVATE
                    )
                    sharedPreferences.edit().putString(Constance.KEY_USER_ID, id).apply()
                    avatar?.let {
                        fileManager.saveImageToInternalStorage(avatar, fileName)
                        localReposetoryHelper.addProfile(user)
                    } ?: saveDefaultImage(context, fileName)
                    response.code
                } else {
                    response.code
                }
            } catch (e: Exception) {
                Log.e("MyLog", "Ошибка при добавлении пользователя: ${e.message}")
                Constance.NETWORK_ERROR
            }
        }

    suspend fun updateProfile(firstName: String, secondName: String, avatar: Uri?): Int =
        withContext(Dispatchers.IO) {
            try {
                val user = _userProfile.value!!
                val fileName = user.avatar
                val updateUserInfo = UpdateUserInfo(firstName, secondName, fileName)
                val response = userCase.updateUserInfo(updateUserInfo, avatar, user.user_id)
                if (response == Constance.SUCCESS) {
                    val newLink = userCase.getLink(user.user_id, fileName)
                    val newInfo = _userProfile.value!!.copy(
                        firstname = firstName,
                        secondname = secondName,
                        avatar = newLink.toString()
                    )
                    withContext(Dispatchers.Main) {
                        _userProfile.postValue(newInfo)
                    }
                    avatar?.let {
                        val newFile = fileManager.updateImageInInternalStorage(avatar, fileName)
                        localReposetoryHelper.updateProfile(firstName, secondName, newFile)
                    }
                    Constance.SUCCESS

                } else Constance.NETWORK_ERROR

            } catch (e: Exception) {
                Log.d("MyLog", "Ошибка updateProfile ${e.message}")
                Constance.NETWORK_ERROR
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

    private fun saveDefaultImage(context: Context, uniqueKey: String): String {
        val defaultFoto = BitmapFactory.decodeResource(context.resources, R.drawable.profile_foro)
        return fileManager.saveBitmapToInternalStorage(defaultFoto, uniqueKey)
    }

    fun deleteFile(fileName: String) {
        fileManager.deleteImageFromInternalStorage(fileName)
    }

    init {
        initUserInfoLocal()
        updateUserEventsList()
        isRecording.value = false
    }


    //NETWORK METHODS
}