package com.example.myapplication.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.elements.ItemChat
import com.example.myapplication.elements.UserForChoose
import com.example.myapplication.reposetory.LocalReposetoryHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.lang.Exception
import java.util.UUID

open class ViewModelForChats(private val localReposetoryHelper: LocalReposetoryHelper, application: Application): AndroidViewModel(application) {
    val ListOfChats:MutableLiveData<List<ItemChat>> = MutableLiveData()

    fun updateChatList() {
        Log.d("MyLog", "Обновление модели чатов")
        
        ListOfChats.value = localReposetoryHelper.GetAllChats()
    }
     fun AddNewChat(context: Context,user:UserForChoose,message:String){

        viewModelScope.launch(Dispatchers.IO) {
            val bitmapFoto = BitmapFactory.decodeResource(context.resources,
                user.foto
            )
//            val interlocutorId = listWhoGetMes[0].id
            val uniqueKey = UUID.randomUUID().toString()
            val currentTime = System.currentTimeMillis()
            Log.d("MyLog","Отправка в БД нового чата")
            val FotoChat = saveImageToInternalStorage(bitmapFoto,uniqueKey)
            val itemChat = ItemChat(uniqueKey, FotoChat, user.nickname, message, currentTime)
            localReposetoryHelper.addChat(itemChat)

            withContext(Dispatchers.Main){
                updateChatList()
            }
        }
    }

    fun AddChats(context: Context,listWhoGetMes:List<UserForChoose>,message:String){
        val NewChatsList = ArrayList<ItemChat>()

        viewModelScope.launch(Dispatchers.IO) {
            for (user in listWhoGetMes) {
                val bitmapFoto = BitmapFactory.decodeResource(context.resources,
                    user.foto
                )
                val uniqueKey = UUID.randomUUID().toString()
                Log.d("MyLog",uniqueKey)
                val currentTime = System.currentTimeMillis()
                val FotoChat = saveImageToInternalStorage(bitmapFoto,uniqueKey)
                NewChatsList.add(ItemChat(uniqueKey, FotoChat, user.nickname, message, currentTime))

            }
//            Log.d("MyLog", "Отправка в БД нового чата")
            localReposetoryHelper.AddNewChats(NewChatsList)

            withContext(Dispatchers.Main) {
                updateChatList()
            }
        }
    }
    fun deleteChat(Chats:List<ItemChat> ){
        viewModelScope.launch(Dispatchers.IO) {
            localReposetoryHelper.ChatDelete(Chats)
            withContext(Dispatchers.Main){
                updateChatList()
            }
            Chats.forEach {
                Log.d("MyLog",it.IDchat)
                deleteImageFromInternalStorage(it.IDchat)
            }
        }

    }

    private fun saveImageToInternalStorage(bitmap: Bitmap, fileName: String):String{
        val fileOutputStream: FileOutputStream
        val context = getApplication<Application>()
        try {
            fileOutputStream = context.openFileOutput("$fileName.jpg",Context.MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.JPEG,80,fileOutputStream)
            fileOutputStream.close()
        } catch (e:Exception){
            e.printStackTrace()
        }
        return context.getFileStreamPath("$fileName.jpg").absolutePath
    }

    private fun deleteImageFromInternalStorage(fileName:String){
         val context = getApplication<Application>()
        val file = context.getFileStreamPath("$fileName.jpg")

        if(file.exists()){
            file.delete()
            Log.d("MyLog", "Изображение $fileName успешно удалено.")
        }else {
            Log.d("MyLog", "Изображение $fileName не существует.")
        }

    }


    init {
        updateChatList()
    }
}