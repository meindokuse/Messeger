package com.example.myapplication.viewmodel
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.ActionMode.Callback
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.api.ApiClient
import com.example.myapplication.elements.Event
import com.example.myapplication.elements.ProfileInfo
import com.example.myapplication.reposetory.LocalReposetoryHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.Nullable
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.UUID

open class MyViewModel(private val localReposetoryHelper: LocalReposetoryHelper,profileId: Long,application: Application): AndroidViewModel(application) {


    val userProfile: LiveData<ProfileInfo> = MutableLiveData()
    val statusMessege: MutableLiveData<String?> = MutableLiveData()


    private fun updateUserProfile(){
        Log.d("MyLog","Инит")
        Log.d("MyLog","${localReposetoryHelper.getAllInfo()}")
        (userProfile as MutableLiveData).value = localReposetoryHelper.getAllInfo()

    }
    fun addUser(context: Context,info:ArrayList<String> ,avatar:Bitmap?){
        val id = UUID.randomUUID().toString()

        if (avatar == null){
           val defaultFoto = BitmapFactory.decodeResource(context.resources,R.drawable.profile_foro)
            val uniqueKey = UUID.randomUUID().toString()
            val fotoForAvatar = saveImageToInternalStorage(defaultFoto,uniqueKey)
            val profileInfo = ProfileInfo(id,info[0],info[1],info[2],info[3],info[4],info[5],fotoForAvatar)
            Log.d("MyLog","Во вьюхе заварушка")
            localReposetoryHelper.addProfile(profileInfo)
        }else {
            val uniqueKey = UUID.randomUUID().toString()
            val fotoForAvatar = saveImageToInternalStorage(avatar, uniqueKey)
            val profileInfo = ProfileInfo(id,info[0], info[1], info[2], info[3], info[4], info[5], fotoForAvatar)
            Log.d("MyLog", "Во вьюхе заварушка")
            localReposetoryHelper.addProfile(profileInfo)
        }
        updateUserProfile()


    }

    fun uppdateProfile(FirstName:String,SecondName:String,avatar: Bitmap?){
        val path = userProfile.value!!.avatar
        val id = userProfile.value!!.idUser

        viewModelScope.launch(Dispatchers.IO) {
            if(avatar != null) {
                val uniqueKey = UUID.randomUUID().toString()
                val targetFoto = updateImageInInternalStorage(avatar,path,uniqueKey)
                localReposetoryHelper.updateProfile(id,FirstName, SecondName, targetFoto)
            }else{
                localReposetoryHelper.updateProfile(id,FirstName, SecondName, path)
            }
            withContext(Dispatchers.Main){
                updateUserProfile()
            }
//            ApiClient.apiServer.getProfile(id).enqueue(object : retrofit2.Callback<ProfileInfo?> {
//                override fun onResponse(call: Call<ProfileInfo?>, response: Response<ProfileInfo?>) {
//                    // Обработка ответа
//                    if (response.isSuccessful) {
//                        val profileInfo = response.body()
//                        // Обработка profileInfo
//                        (userProfile as MutableLiveData).value = profileInfo
//                        statusMessege.value = null
//                    } else {
//                        statusMessege.value = "Ошибка получения данных"
//                    }
//                }
//
//                override fun onFailure(call: Call<ProfileInfo?>, t: Throwable) {
//                    statusMessege.value = "Офлайн режим"
//                }
//            })

        }

    }




    //makes for RcView ( events )
    val userEvents: LiveData<List<Event>> = MutableLiveData()
    val UserEventRightNow:MutableLiveData<Event> = MutableLiveData()

    fun UdpateUserEventRightNow(event: Event){
        UserEventRightNow.value = event

    }

    fun addEventToReposetory(event: Event,profileId:Long){
        localReposetoryHelper.addEventForRcView(event,profileId)
        updateUserEventsList(profileId)
        UserEventRightNow.value = event


    }
    fun updateUserEventsList(profileId: Long){
        Log.d("MyLog","Лист выгружен")

        (userEvents as MutableLiveData).value = localReposetoryHelper.getAllEvents(profileId)
        Log.d("MyLog","${userEvents.value}")
    }

    fun DeleteEvent(event: Event,profileId: Long){
        localReposetoryHelper.deleteAll(event)
        updateUserEventsList(profileId)
    }
    init {
        updateUserProfile()
        updateUserEventsList(profileId)
    }

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

    private fun updateImageInInternalStorage(newBitmap: Bitmap,oldName:String, fileName: String): String {

        // Удаляем старый файл
        deleteImageFromInternalStorage(oldName)

        // Сохраняем новый файл
        return saveImageToInternalStorage(newBitmap, fileName)
    }
    private fun deleteImageFromInternalStorage(fileName: String) {
        val context = getApplication<Application>()
        val file = File(fileName)

        if (file.exists()) {
            file.delete()
            Log.d("MyLog", "Изображение $fileName успешно удалено.")
        } else {
            Log.d("MyLog", "Изображение $fileName не существует.")
        }
    }


}