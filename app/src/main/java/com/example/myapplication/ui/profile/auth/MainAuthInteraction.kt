package com.example.myapplication.ui.profile.auth

interface MainAuthInteraction {

    fun goReg()
    fun goLogin()
    suspend fun regUser(profile:ArrayList<String>):Int
    suspend fun loginUser(email:String,password:String):Int
}