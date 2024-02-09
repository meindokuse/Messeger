package com.example.myapplication.ui.profile.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.reposetory.profile.LocalProfileReposetoryImpl
import com.example.myapplication.data.reposetory.profile.RemoteUserReposImpl
import com.example.myapplication.util.Constance
import com.example.myapplication.models.Event
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.models.UpdateUserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
open class ProfileViewModel @Inject constructor(
    private val remoteProfileReposetory: RemoteUserReposImpl,
    private val localProfileReposetory: LocalProfileReposetoryImpl,
) : ViewModel() {


    private val _userProfile: MutableLiveData<ProfileInfo> = MutableLiveData()
    val userProfile: LiveData<ProfileInfo> get() = _userProfile

    private fun initUserInfoLocal() {
        val profileInfo = localProfileReposetory.getUserInfoLocal()
        _userProfile.postValue(profileInfo)
    }


    suspend fun syncUser(userId: String): Int {
        return try {
            val response = remoteProfileReposetory.getUserInfo(userId)
            if (response.data != null) {
                val user = response.data
                val fileName = user.avatar
                val link = remoteProfileReposetory.getUri(userId,fileName)
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

                val response = remoteProfileReposetory.regNewUser(info, avatar, id, fileName)
                if (response.data != null) {
                    val sharedPreferences = context.getSharedPreferences(
                        Constance.KEY_USER_PREFERENCES,
                        Context.MODE_PRIVATE
                    )
                    sharedPreferences.edit().putString(Constance.KEY_USER_ID, id).apply()
                    localProfileReposetory.addUser(response.data)
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
                val response = remoteProfileReposetory.updateUserInfo(updateUserInfo, avatar, user.user_id)
                if (response == Constance.SUCCESS) {
                    val newLink = remoteProfileReposetory.getUri(user.user_id,fileName)
                    val newInfo = _userProfile.value!!.copy(
                        firstname = firstName,
                        secondname = secondName,
                        avatar = newLink.toString()
                    )
                    withContext(Dispatchers.Main) {
                        _userProfile.postValue(newInfo)
                    }
                    Constance.SUCCESS

                } else Constance.NETWORK_ERROR

            } catch (e: Exception) {
                Log.d("MyLog", "Ошибка updateProfile ${e.message}")
                Constance.NETWORK_ERROR
            }
        }


    //makes for RcView ( events )

    private val _userEvents: MutableLiveData<List<Event>> = MutableLiveData()

    val userEvents: LiveData<List<Event>> = _userEvents
    val UserEventRightNow: MutableLiveData<Event> = MutableLiveData()


    fun addEventToReposetory(event: Event) {
        localProfileReposetory.addEvent(event)
        updateUserEventsList()
        UserEventRightNow.value = event
    }

    private fun updateUserEventsList() {
        Log.d("MyLog", "Лист выгружен")
        val events = localProfileReposetory.initEvents()
        _userEvents.postValue(events)
        Log.d("MyLog", "${userEvents.value}")
    }

    fun DeleteEvent(event: Event) {
        localProfileReposetory.deleteEvent(event)

        updateUserEventsList()
    }

    // FOR SOUND EVENTS (POSTS)


    val isRecording: MutableLiveData<Boolean> = MutableLiveData()


    fun startRecording(): String {
        isRecording.postValue(true)
        return localProfileReposetory.startRecordAudio()
    }

    fun stopRecording() {
        isRecording.postValue(false)
        localProfileReposetory.stopRecordAudio()
    }

    fun deleteFile(fileName:String){
        localProfileReposetory.deleteAudio(fileName)
    }

    // for image in profile and soundFormat(posts)
    init {
        initUserInfoLocal()
        updateUserEventsList()
        isRecording.value = false
    }


    //NETWORK METHODS
}