package com.example.myapplication.domain.mapers

import com.example.myapplication.data.models.local.ChatEntity
import com.example.myapplication.data.models.local.MessageEntity
import com.example.myapplication.data.models.local.ProfileEntity
import com.example.myapplication.data.models.remote.ProfileDto
import com.example.myapplication.models.ItemChat
import com.example.myapplication.models.MessageInChat
import com.example.myapplication.models.ProfileInfo


fun ChatEntity.toItemChat(): ItemChat =
    ItemChat(
        chat_id = chatId,
        user_id_1 = userId1,
        user_id_2 = userId2,
        avatar = avatar,
        nickname = nickname,
        mes_text = mesText,
        mes_time = mesTime
    )

fun ProfileEntity.toProfileInfo():ProfileInfo =
    ProfileInfo(
        user_id = userId,
        firstname = firstname,
        secondname = secondname,
        school = school,
        city = city,
        age = age,
        targetClass, avatar, email, password
    )

fun MessageEntity.toMessageInChat(): MessageInChat =
    MessageInChat(
        message_id = messageId,
        id_sender = idSender,
        id_chat = idChat,
        content, time, type
    )



