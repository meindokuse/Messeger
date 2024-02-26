package com.example.myapplication.di.datasources

import com.example.myapplication.data.remote.MessagesSocket
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(Logging)
            install(WebSockets){
                pingInterval = 20_000
            }

        }
    }
    @Provides
    @Singleton
    fun provideMessageService(client: HttpClient): MessagesSocket {
        return MessagesSocket(client)
    }
}