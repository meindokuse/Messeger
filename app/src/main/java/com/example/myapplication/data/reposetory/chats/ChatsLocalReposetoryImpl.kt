package com.example.myapplication.data.reposetory.chats

import android.graphics.Bitmap
import androidx.core.net.toUri
import com.example.myapplication.data.local.FileManager
import com.example.myapplication.data.local.LocalReposetoryHelper
import com.example.myapplication.domain.reposetory.chats.ChatLocalReposetory
import com.example.myapplication.models.ItemChat

//class ChatsLocalReposetoryImpl(
//    private val localReposetoryHelper: LocalReposetoryHelper,
//    private val fileManager: FileManager
//): ChatLocalReposetory {
//    override fun initChats(): List<ItemChat>? {
//       return localReposetoryHelper.GetAllChats()
//    }
//
//    override fun createNewChat(listChats: List<ItemChat>,) {
//        localReposetoryHelper.AddNewChats(listChats)
//        listChats.forEach {
//            fileManager.saveImageToInternalStorage(it.avatar.toUri(),it.avatar)
//        }
//    }
//    override fun updateChat(itemChat: ItemChat) {
//        localReposetoryHelper.updateChat(itemChat)
//    }
//
//}