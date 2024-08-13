package com.example.friendnet.domain.reposetory.chats

import com.example.friendnet.models.ItemChat

interface ChatLocalReposetory {

    fun initChats(): List<ItemChat>?

    fun createNewChat(listChats: List<ItemChat>)
    fun updateChat(itemChat: ItemChat)
}