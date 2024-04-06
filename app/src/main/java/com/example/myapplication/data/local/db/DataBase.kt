package com.example.myapplication.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.data.local.db.dao.ChatDao
import com.example.myapplication.data.local.db.dao.ChatRemoteKeysDao
import com.example.myapplication.data.local.db.dao.MessageDao
import com.example.myapplication.data.local.db.dao.MessageRemoteKeysDao
import com.example.myapplication.data.local.db.dao.ProfileDao
import com.example.myapplication.data.models.local.ChatEntity
import com.example.myapplication.data.models.local.MessageEntity
import com.example.myapplication.data.models.local.ProfileEntity
import com.example.myapplication.data.models.local.RemoteKeysChat
import com.example.myapplication.data.models.local.RemoteKeysMessage


@Database(
    entities = [ProfileEntity::class,ChatEntity::class,MessageEntity::class,RemoteKeysMessage::class, RemoteKeysChat::class],
    version = 1
)
abstract class DataBase: RoomDatabase() {

    abstract val profileDao: ProfileDao
    abstract val messageDao: MessageDao
    abstract val chatDao: ChatDao
    abstract val chatKeysDao: ChatRemoteKeysDao
    abstract val messageKeysDao: MessageRemoteKeysDao
}