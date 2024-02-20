package com.example.myapplication.di.modules

import com.example.myapplication.data.remote.FirebaseStorage
import com.example.myapplication.data.remote.MessagesSocket
import com.example.myapplication.data.reposetory.chats.ChatsReposetoryImpl
import com.example.myapplication.data.reposetory.message.RemoteMessagesReposetoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
object MessagesModule {

    @Provides
    @ActivityRetainedScoped
    fun provideRemoteMessagesReposetory(messagesSocket: MessagesSocket): RemoteMessagesReposetoryImpl {
        return RemoteMessagesReposetoryImpl(messagesSocket)
    }


}