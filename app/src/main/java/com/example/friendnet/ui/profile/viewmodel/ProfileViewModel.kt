package com.example.friendnet.ui.profile.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.friendnet.data.models.remote.ProfileDto
import com.example.friendnet.data.reposetory.profile.LocalProfileReposetoryImpl
import com.example.friendnet.data.reposetory.profile.RemoteUserReposImpl
import com.example.friendnet.models.Event
import com.example.friendnet.models.ProfileInfo
import com.example.friendnet.domain.mapers.toProfileInfo
import com.example.friendnet.util.Constance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
open class ProfileViewModel @Inject constructor(
    private val remoteProfileReposetory: RemoteUserReposImpl,
    private val localProfileReposetory: LocalProfileReposetoryImpl,
) : ViewModel() {


    private val _userProfile: MutableLiveData<ProfileInfo> = MutableLiveData()
    val userProfile: LiveData<ProfileInfo> get() = _userProfile

    private fun initUserInfoLocal() {
        viewModelScope.launch(Dispatchers.IO) {
            val profileInfo = localProfileReposetory.getUserInfoLocal()
            withContext(Dispatchers.Main) {
                _userProfile.postValue(profileInfo)

            }
        }
    }


    suspend fun syncUser(userId: String): Int {
        return try {
            val response = remoteProfileReposetory.getUserInfo(userId)
            if (response.data != null) {
                val user = response.data
                withContext(Dispatchers.Main) {
                    _userProfile.postValue(user.toProfileInfo())
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

    suspend fun addUser(context: Context, info: ArrayList<String>): Int =
        try {
            val id = UUID.randomUUID().toString().reversed()
            val fileName = "avatar_${UUID.randomUUID()}.jpg"

            val response = remoteProfileReposetory.regNewUser(info, id, fileName)
            Log.d("MyLog","reg user ${response.data} ${response.code}")
            if (response.data != null) {
                withContext(Dispatchers.Main) {
                    val sharedPreferences = context.getSharedPreferences(
                        Constance.KEY_USER_PREFERENCES,
                        Context.MODE_PRIVATE
                    )
                    sharedPreferences.edit().putString(Constance.KEY_USER_ID, id).apply()
                }
                localProfileReposetory.addUser(response.data)
                response.code
            } else {
                response.code
            }
        } catch (e: Exception) {
            Log.e("MyLog", "Ошибка при добавлении пользователя: ${e.message}")
            Constance.NETWORK_ERROR
        }


    suspend fun loginUser(context: Context, email: String, password: String): Int =
        withContext(Dispatchers.IO) {
            try {
                val status = remoteProfileReposetory.loginUser(email, password)
                if (status?.status == Constance.SUCCESS) {
                    withContext(Dispatchers.Main) {
                        val sharedPreferences = context.getSharedPreferences(
                            Constance.KEY_USER_PREFERENCES,
                            Context.MODE_PRIVATE
                        )
                        sharedPreferences.edit().putString(Constance.KEY_USER_ID, status.idUser)
                            .apply()
                    }
                    status.status
                } else status!!.status
            } catch (e: Exception) {
                Log.d("MyLog", e.message.toString())
                Constance.NETWORK_ERROR
            }
        }


    suspend fun updateProfile(profileDto: ProfileDto, avatar: Uri?): Int =
        withContext(Dispatchers.IO) {
            try {
                Log.d("MyLog", "updateProfile - $avatar")
                val response = remoteProfileReposetory.updateUserInfo(profileDto, avatar)
                Log.d("MyLog", "$profileDto")
                if (response == Constance.SUCCESS) {
                    val newInfo = _userProfile.value!!.copy(
                        firstname = profileDto.firstname,
                        secondname = profileDto.secondname,
                    )
                    withContext(Dispatchers.Main) {
                        _userProfile.postValue(newInfo)
                    }
                    Log.d("MyLog", "успещная обновва")
                    localProfileReposetory.updateUserInfo(profileDto, avatar)

                    Constance.SUCCESS

                } else Constance.NETWORK_ERROR

            } catch (e: Exception) {
                Log.d("MyLog", "Ошибка updateProfile ${e.message}")
                Constance.NETWORK_ERROR
            }
        }


    suspend fun getLinkToFile(userId: String, fileName: String): Uri? {

        return try {
            remoteProfileReposetory.getUri(userId, fileName)
        } catch (e: Exception) {
            Log.d("MyLog", "getLinkToFile error $e")
            null
        }
    }


    //makes for RcView ( events )

    private val _userEvents: MutableLiveData<List<Event>> = MutableLiveData()
    val userEvents: LiveData<List<Event>> = _userEvents

    val UserEventRightNow: MutableLiveData<Event> = MutableLiveData()


    fun addEventToReposetory(event: Event) {
        viewModelScope.launch {
            localProfileReposetory.addEvent(event)
            updateUserEventsList()
            withContext(Dispatchers.Main) {
                UserEventRightNow.postValue(event)
            }
        }

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

    fun deleteFile(fileName: String) {
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