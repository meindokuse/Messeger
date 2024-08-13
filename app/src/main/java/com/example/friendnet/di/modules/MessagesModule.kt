package com.example.friendnet.di.modules

import com.example.friendnet.data.remote.FirebaseStorage
import com.example.friendnet.data.remote.MessagesSocket
import com.example.friendnet.data.reposetory.message.RemoteMessagesReposetoryImpl
import com.example.friendnet.util.api.NetworkApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object MessagesModule {

    @Provides
    @ActivityRetainedScoped
    fun provideRemoteMessagesReposetory(userApi: NetworkApi,firebaseStorage: FirebaseStorage,messagesSocket: MessagesSocket): RemoteMessagesReposetoryImpl {
        return RemoteMessagesReposetoryImpl(userApi,firebaseStorage,messagesSocket)
    }


}