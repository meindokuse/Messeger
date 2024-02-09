package com.example.myapplication.domain.reposetory.chats

import android.net.Uri
import com.example.myapplication.models.ItemChat
import com.example.myapplication.models.ProfileInfo
import com.example.myapplication.models.UpdateUserInfo

interface ChatLocalReposetory {

    fun initChats(): List<ItemChat>?

    fun createNewChat(listChats: List<ItemChat>)
    fun updateChat(itemChat: ItemChat)
}