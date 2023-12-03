package com.example.myapplication

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.reposetory.LocalReposetoryHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

open class ViewModelForChats(private val localReposetoryHelper: LocalReposetoryHelper): ViewModel() {
    val ListOfChats:MutableLiveData<List<ItemChat>> = MutableLiveData()

    fun updateChatList(){
        ListOfChats.value = localReposetoryHelper.GetAllChats()
    }
    fun AddNewChat(IDchat:String,Foto:Bitmap,NameSender:String,LastMes:String,Date:Long){
        viewModelScope.launch(Dispatchers.IO) {
            val outputStream = ByteArrayOutputStream()
            Foto.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val fotoByteArray = outputStream.toByteArray()
            val itemChat = ItemChat(IDchat, fotoByteArray, NameSender, LastMes, Date)
            localReposetoryHelper.addChat(itemChat)

            withContext(Dispatchers.Main) {
                updateChatList()
            }

        }
    }
    fun deleteChat(itemChat: ItemChat){
        localReposetoryHelper.ChatDelete(itemChat)
    }

    init {
        updateChatList()
    }
}