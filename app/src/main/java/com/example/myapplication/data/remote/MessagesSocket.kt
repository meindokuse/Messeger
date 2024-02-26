package com.example.myapplication.data.remote

import android.util.Log
import com.example.myapplication.models.MessageInChat
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.http.ContentType.Application.Json
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.send
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow


class MessagesSocket(
    private val client: HttpClient
) {


    private var socket: WebSocketSession? = null


    suspend fun connect(idChat: String) {
        socket = client.webSocketSession {
            url("ws://79.174.81.166:8000/ws/$idChat")
        }
    }


    fun disconnect() {
        client.close()
    }

    suspend fun sendMessage(idChat: String, message: MessageInChat): Boolean {
        val messageJson = Gson().toJson(message)
        return try {
            socket?.send(messageJson)
            true // Успешно отправлено
        } catch (e: Exception) {
            Log.d("MyLog", "sendMessage in source $e")
            false // Произошла ошибка при отправке
        }
    }

    fun observeMessages(): Flow<MessageInChat> {
        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val message = parseMessageInChat(json)
                    message
                } ?: flow { }
        } catch (e: Exception) {
            e.printStackTrace()
            flow { }
        }
    }


    private fun parseMessageInChat(json: String): MessageInChat {
        return Gson().fromJson(json, MessageInChat::class.java)
    }


}