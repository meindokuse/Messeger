package com.example.friendnet.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.friendnet.data.local.db.dao.ChatDao
import com.example.friendnet.data.local.db.dao.ChatRemoteKeysDao
import com.example.friendnet.data.local.db.dao.MessageDao
import com.example.friendnet.data.local.db.dao.MessageRemoteKeysDao
import com.example.friendnet.data.local.db.dao.ProfileDao
import com.example.friendnet.data.models.local.ChatEntity
import com.example.friendnet.data.models.local.MessageEntity
import com.example.friendnet.data.models.local.ProfileEntity
import com.example.friendnet.data.models.local.RemoteKeysChat
import com.example.friendnet.data.models.local.RemoteKeysMessage


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