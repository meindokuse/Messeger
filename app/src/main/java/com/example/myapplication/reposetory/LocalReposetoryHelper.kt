package com.example.myapplication.reposetory

import android.content.Context
import android.util.Log
import com.example.myapplication.models.ItemChat
import com.example.myapplication.models.Event
import com.example.myapplication.models.ProfileInfo


class LocalReposetoryHelper(context: Context) {
    private val localReposetory: LocalReposetory = LocalReposetory(context)
    //makes for profile

    fun getUserId():String{
       return localReposetory.getUserId()
    }
    fun getAllInfo(): ProfileInfo {
        return localReposetory.getAll()
    }

    fun addProfile(profileInfo: ProfileInfo){
        Log.d("MyLog", "Updating user profile")
        localReposetory.addOrChangeProfile(profileInfo)
    }
    fun updateProfile(id:String,FirstName:String,SecondName:String,avatar:String){
        localReposetory.updateProfile(id,FirstName,SecondName,avatar)
    }

    // makes for RecyclerView ( events )
    fun addEventForRcView(event: Event,){
        localReposetory.addEvent(event)
    }
    fun getAllEvents(): List<Event>{
        return localReposetory.getAllEvents().reversed()
    }
    fun deleteAll(event: Event){
        localReposetory.deleteEvent(event)
    }
    // FOR CHATS
    fun addChat(itemChat: ItemChat){
        localReposetory.addChat(itemChat)
    }
    fun GetAllChats():List<ItemChat>{
        return localReposetory.getListChats()
    }
    fun ChatDelete(Chats:List<ItemChat>){
        Chats.forEach {
            localReposetory.removeChat(it)

        }
    }
    fun AddNewChats(Chats:List<ItemChat>){
        Chats.forEach{
            localReposetory.addChat(it)
        }
    }

}