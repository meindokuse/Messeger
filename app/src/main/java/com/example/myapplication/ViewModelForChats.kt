package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.reposetory.LocalReposetoryHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

open class ViewModelForChats(private val localReposetoryHelper: LocalReposetoryHelper): ViewModel() {
    val ListOfChats:MutableLiveData<List<ItemChat>> = MutableLiveData()

    fun updateChatList() {
        Log.d("MyLog", "Обновление модели чатов")
        
        ListOfChats.value = localReposetoryHelper.GetAllChats()
    }
     fun AddNewChat(IDchat:String,Foto:Bitmap,NameSender:String,LastMes:String,Date:Long){

        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MyLog","Отправка в БД нового чата")

            val outputStream = ByteArrayOutputStream()
            Foto.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val fotoByteArray = outputStream.toByteArray()
            val itemChat = ItemChat(IDchat, fotoByteArray, NameSender, LastMes, Date)
            localReposetoryHelper.addChat(itemChat)

            withContext(Dispatchers.Main){
                updateChatList()
            }

        }


    }
    fun AddChats(context: Context,listWhoGetMes:ArrayList<String>,message:String){
        val NewChatsList = ArrayList<ItemChat>()

        viewModelScope.launch(Dispatchers.IO) {
            for (i in listWhoGetMes) {
                val bitmapFoto = BitmapFactory.decodeResource(context.resources, R.drawable.profile_foro)
                val uniqueKey = UUID.randomUUID().toString()
                val currentTime = System.currentTimeMillis()
                val outputStream = ByteArrayOutputStream()
                bitmapFoto.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                val fotoByteArray = outputStream.toByteArray()
                NewChatsList.add(ItemChat(uniqueKey, fotoByteArray, i, message, currentTime))

            }
            Log.d("MyLog", "Отправка в БД нового чата")
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
        }

    }

    init {
        updateChatList()
    }
}