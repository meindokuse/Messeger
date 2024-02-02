package com.example.myapplication.profile.domain

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaRecorder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Constance
import com.example.myapplication.R
import com.example.myapplication.models.Event
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.profile.AudioRecorder
import com.example.myapplication.reposetory.LocalReposetoryHelper
import com.example.myapplication.reposetory.RemoteReposetory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.UUID

open class MyViewModel(
    private val localReposetoryHelper: LocalReposetoryHelper,
    profileId: Long,
    application: Application
) : AndroidViewModel(application) {


    val userProfile: LiveData<ProfileInfo> = MutableLiveData()


    private fun updateUserProfile() {
        Log.d("MyLog", "Инит")
        Log.d("MyLog", "${localReposetoryHelper.getAllInfo()}")
        (userProfile as MutableLiveData).value = localReposetoryHelper.getAllInfo()
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

    suspend fun addUser(context: Context, info: ArrayList<String>, avatar: Bitmap?): Int {
        val id = UUID.randomUUID().toString().reversed()
        return if (avatar == null) {
            val defaultFoto =
                BitmapFactory.decodeResource(context.resources, R.drawable.profile_foro)
            val uniqueKey = UUID.randomUUID().toString()
            val fotoForAvatar = saveImageToInternalStorage(defaultFoto, uniqueKey)
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
            Log.d("MyLog", "$info")
            Log.d("MyLog", "$profileInfo")
            val response = RemoteReposetory.regUser(profileInfo)
            if (response.isSuccessful) {
                val sharedPreferences =
                    context.getSharedPreferences(Constance.KEY_USER_PREFERENCES, Context.MODE_PRIVATE)
                sharedPreferences.edit().putString(Constance.KEY_USER_ID, id).apply()
                localReposetoryHelper.addProfile(profileInfo)
                updateUserProfile()

                200
            } else {
                deleteImageFromInternalStorage(uniqueKey)
                400
            }

        } else {
            val uniqueKey = UUID.randomUUID().toString()
            val fotoForAvatar = saveImageToInternalStorage(avatar, uniqueKey)
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

                val sharedPreferences = context.getSharedPreferences(Constance.KEY_USER_PREFERENCES, Context.MODE_PRIVATE)

                sharedPreferences.edit()
                    .putString(Constance.KEY_USER_ID, id)
                    .apply()
                localReposetoryHelper.addProfile(profileInfo)
                updateUserProfile()
                200
            } else {
                deleteImageFromInternalStorage(uniqueKey)
                400
            }
        }
    }

    fun uppdateProfile(FirstName: String, SecondName: String, avatar: Bitmap?) {
        val path = userProfile.value!!.avatar
        val id = userProfile.value!!.user_id

        viewModelScope.launch(Dispatchers.IO) {
            if (avatar != null) {
                val uniqueKey = UUID.randomUUID().toString()
                val targetFoto = updateImageInInternalStorage(avatar, path, uniqueKey)
                localReposetoryHelper.updateProfile(id, FirstName, SecondName, targetFoto)
            } else {
                localReposetoryHelper.updateProfile(id, FirstName, SecondName, path)
            }
            withContext(Dispatchers.Main) {
                updateUserProfile()
            }
        }
    }


    //makes for RcView ( events )

    val userEvents: LiveData<List<Event>> = MutableLiveData()
    val UserEventRightNow: MutableLiveData<Event> = MutableLiveData()

//    fun UdpateUserEventRightNow(event: Event){
//        UserEventRightNow.value = event
//    }

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
            deleteSoundFromEvent(event.desc)
        }
        updateUserEventsList()
    }

    // FOR SOUND EVENTS (POSTS)

    val audioRecorder = AudioRecorder(application)

    private var mediaRecorder: MediaRecorder? = null

    val isRecording: MutableLiveData<Boolean> = MutableLiveData()


    @Suppress("DEPRECATION")
    fun startRecording(): String {
        // Проверяем, что запись не выполняется в данный момент
        if (mediaRecorder != null) {
            stopRecording()
        }

        val audioFilePath =
            "${getApplication<Application>().filesDir}/audio_${UUID.randomUUID()}.3gp"

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44100) // Частота дискретизации 44.1 kHz (стандарт для CD-качества)
            setAudioEncodingBitRate(320000) // Битрейт 128 kbps (стандарт для хорошего качества звука)
            setOutputFile(audioFilePath)

            try {
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("YourAudioRecordingClass", "Ошибка при подготовке к записи: ${e.message}")
            }
        }

        return audioFilePath
    }

    fun stopRecording() {

        try {
            mediaRecorder?.apply {
                stop()
                release()
            }

        } catch (e: RuntimeException) {
            Log.e("YourAudioRecordingClass", "Ошибка при остановке записи: ${e.message}")
        }
        mediaRecorder = null
    }


    override fun onCleared() {
        super.onCleared()
        mediaRecorder?.release()
    }

    init {
        updateUserProfile()
        updateUserEventsList()
        isRecording.value = false
    }

    // for image in profile and soundFormat(posts)


    private fun saveImageToInternalStorage(bitmap: Bitmap, fileName: String): String {
        val fileOutputStream: FileOutputStream
        val context = getApplication<Application>()
        val file = File(context.filesDir, "$fileName.jpg")

        try {
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file.absolutePath
    }

    private fun updateImageInInternalStorage(
        newBitmap: Bitmap,
        oldName: String,
        fileName: String
    ): String {

        // Удаляем старый файл
        deleteImageFromInternalStorage(oldName)

        // Сохраняем новый файл
        return saveImageToInternalStorage(newBitmap, fileName)
    }

    private fun deleteImageFromInternalStorage(fileName: String) {
        val file = File(fileName)

        if (file.exists()) {
            file.delete()
            Log.d("MyLog", "Изображение $fileName успешно удалено.")
        } else {
            Log.d("MyLog", "Изображение $fileName не существует.")
        }
    }

    fun deleteSoundFromEvent(fileName: String) {
        val file = File(fileName)

        if (file.exists()) {
            file.delete()
            Log.d("MyLog", "Аудио $fileName успешно удалено.")
        } else {
            Log.d("MyLog", "Аудио $fileName не существует.")
        }
    }
    //NETWORK METHODS
}