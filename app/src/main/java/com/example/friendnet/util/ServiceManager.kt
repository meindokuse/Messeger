package com.example.friendnet.util

object ServiceManager {

    private var boundService: AudioService? = null

    fun setBoundService(service: AudioService) {
        boundService = service
    }
    fun clearFordisconect(){
        boundService?.clearMedia()
        boundService = null
    }

    fun deleteBoundService(){
        boundService = null
    }
}