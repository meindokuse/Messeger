package com.example.myapplication.di.modules

import com.example.myapplication.data.remote.FirebaseStorage
import com.example.myapplication.data.reposetory.chats.ChatsReposetoryImpl
import com.example.myapplication.domain.reposetory.chats.ChatRepository
import com.example.myapplication.util.api.NetworkApi
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