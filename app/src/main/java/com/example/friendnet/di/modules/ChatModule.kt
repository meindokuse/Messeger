package com.example.friendnet.di.modules

import com.example.friendnet.data.remote.FirebaseStorage
import com.example.friendnet.data.reposetory.chats.ChatsReposetoryImpl
import com.example.friendnet.domain.reposetory.chats.ChatRepository
import com.example.friendnet.util.api.NetworkApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideRemoteChatsRepository(userApi:NetworkApi,firebaseStorage: FirebaseStorage): ChatRepository {
        return ChatsReposetoryImpl(userApi, firebaseStorage) // Предполагается, что ChatsReposetoryImpl реализует интерфейс RemoteChatsReposetory
    }

}