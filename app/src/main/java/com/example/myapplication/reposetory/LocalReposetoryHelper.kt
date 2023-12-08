package com.example.myapplication.reposetory

import android.content.Context
import android.util.Log
import com.example.myapplication.ItemChat
import com.example.myapplication.elements.Event
import com.example.myapplication.profile.ProfileInfo
import androidx.fragment.app.FragmentTransaction


class LocalReposetoryHelper(context: Context) {
    private val localReposetory: LocalReposetory = LocalReposetory(context)
    //makes for profile
    fun getAllInfo(): ProfileInfo {
        return localReposetory.getAll()
    }

    fun addProfile(profileInfo: ProfileInfo){
        Log.d("MyLog", "Updating user profile")
        localReposetory.addOrChangeProfile(profileInfo)
    }
    fun updateProfile(FirstName:String,SecondName:String){
        localReposetory.updateProfile(FirstName,SecondName)
    }

    // makes for RecyclerView ( events )
    fun addEventForRcView(event: Event,profileID:Long){
        localReposetory.addEvent(event,profileID)
    }
    fun getAllEvents(profileID: Long): List<Event>{
        return localReposetory.getAllEvents(profileID).reversed()
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