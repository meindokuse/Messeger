package com.example.friendnet.data.local

import android.content.Context
import android.util.Log
import com.example.friendnet.models.ItemChat
import com.example.friendnet.models.Event
import com.example.friendnet.models.ProfileInfo


class LocalReposetoryHelper(context: Context) {
    private val localReposetory: LocalReposetory = LocalReposetory(context)
    //makes for profile

    fun getUserId(): String {
        return localReposetory.getUserId()
    }

    fun getAllInfo(): ProfileInfo {
        return localReposetory.getAll()
    }

    fun addProfile(profileInfo: ProfileInfo) {
        Log.d("MyLog", "Updating user profile")
        localReposetory.addOrChangeProfile(profileInfo)
    }

    fun updateProfile(FirstName: String, SecondName: String) {
        localReposetory.updateProfile(FirstName, SecondName)
    }

    // makes for RecyclerView ( events )
    fun addEventForRcView(event: Event) {
        localReposetory.addEvent(event)
    }

    fun getAllEvents(): List<Event> {
        return localReposetory.getAllEvents().reversed()
    }

    fun deleteEvent(event: Event) {
        localReposetory.deleteEvent(event)
    }

    // FOR CHATS
    fun addChat(itemChat: ItemChat) {
        localReposetory.addChat(itemChat)
    }

    fun GetAllChats(): List<ItemChat> {
        return localReposetory.getListChats()
    }

    fun ChatDelete(Chats: List<ItemChat>) {
        Chats.forEach {
            localReposetory.removeChat(it)

        }
    }

    fun updateChat(itemChat: ItemChat) {
        localReposetory.updateChat(itemChat)
    }

    fun AddNewChats(Chats: List<ItemChat>) {
        Chats.forEach {
            localReposetory.addChat(it)
        }
    }

}