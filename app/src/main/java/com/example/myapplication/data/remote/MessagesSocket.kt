package com.example.myapplication.data.remote

import android.util.Log
import com.example.myapplication.models.MessageInChat
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach


class MessagesSocket {

    private val client = HttpClient {
        install(WebSockets)
        // Установка тайм-аута на чтение (например, 60 секунд)

    }

    suspend fun connect(idChat: String, messageCallback: (MessageInChat) -> Unit) {
        client.webSocket(
            "ws://79.174.81.166:8000/ws/$idChat"

        ) {
            incoming.consumeEach { frame ->
                val messageJson = (frame as Frame.Text).readText()
                val message = parseMessageInChat(messageJson)
                messageCallback(message)
            }
        }
    }

    fun disconnect() {
        client.close()
    }

    suspend fun sendMessage(idChat: String, message: MessageInChat): Boolean {
        val messageJson = Gson().toJson(message)
        return try {
            client.webSocket("ws://79.174.81.166:8000/ws/$idChat") {
                send(Frame.Text(messageJson))
            }
            true // Успешно отправлено
        } catch (e: Exception) {
            Log.d("MyLog","sendMessage in source $e")
            false // Произошла ошибка при отправке
        }
    }

    private fun parseMessageInChat(json: String): MessageInChat {
        return Gson().fromJson(json, MessageInChat::class.java)
    }
}