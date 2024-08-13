package com.example.friendnet.util

interface AudioInterface {
    fun onClickStartListen(position: Int)

    fun onClickStopListen(position: Int)

    fun seekMediaPlayerTo(progress: Int)

    fun checkIsPlaying():Boolean
}