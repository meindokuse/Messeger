package com.example.myapplication.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.profile.ProfileInfo
import com.example.myapplication.reposetory.LocalReposetoryHelper

open class MyViewModel(private val localReposetoryHelper: LocalReposetoryHelper):ViewModel() {


    val userProfile: LiveData<ProfileInfo> = MutableLiveData()
//    val FirstName: MutableLiveData<String> = MutableLiveData()
//    val SecondName: MutableLiveData<String> = MutableLiveData()

    init {
        updateUserProfile()
    }

    private fun updateUserProfile(){
        Log.d("MyLog","Инит")
        Log.d("MyLog","${localReposetoryHelper.getAllInfo()}")
        (userProfile as MutableLiveData).value = localReposetoryHelper.getAllInfo()
    }
    fun addUser(profileInfo: ProfileInfo){
        Log.d("MyLog","Во вьюхе заварушка")
        localReposetoryHelper.addProfile(profileInfo)
        updateUserProfile()
    }
    fun uppdateProfile(FirstName:String,SecondName:String){
        localReposetoryHelper.updateProfile(FirstName,SecondName)
        updateUserProfile()
    }

}