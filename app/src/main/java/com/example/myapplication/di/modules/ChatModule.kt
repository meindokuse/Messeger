package com.example.myapplication.di.modules

import com.example.myapplication.data.local.FileManager
import com.example.myapplication.data.local.LocalReposetoryHelper
import com.example.myapplication.data.remote.FirebaseStorage
import com.example.myapplication.data.reposetory.chats.ChatsReposetoryImpl
import com.example.myapplication.data.reposetory.profile.LocalProfileReposetoryImpl
import com.example.myapplication.data.reposetory.profile.RemoteUserReposImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
object ChatModule {

    @Provides
    @ActivityRetainedScoped
    fun provideRemoteChatReposetory(firebaseStorage: FirebaseStorage): ChatsReposetoryImpl {
        return ChatsReposetoryImpl(firebaseStorage)
    }

}