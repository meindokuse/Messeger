package com.example.myapplication.data.maper

import com.example.myapplication.data.models.local.ChatEntity
import com.example.myapplication.data.models.local.MessageEntity
import com.example.myapplication.data.models.local.ProfileEntity
import com.example.myapplication.data.models.local.UserForChooseEntity
import com.example.myapplication.data.models.remote.ChatDto
import com.example.myapplication.data.models.remote.MessageDto
import com.example.myapplication.data.models.remote.ProfileDto
import com.example.myapplication.data.models.remote.UserForChooseDto


fun ProfileDto.toProfileEntity(): ProfileEntity =
    ProfileEntity(
        userId = user_id,
        firstname = firstname,
        secondname = secondname,
        school = school,
        city = city,
        age = age,
        targetClass = targetClass,
        avatar = avatar,
        email = email,
        password = password
    )

fun ChatDto.toChatEntity(): ChatEntity =
    ChatEntity(
        chatId = chat_id,
        userId1 = user_id_1,
        userId2 = user_id_2,
        avatar = avatar,
        nickname = nickname,
        mesText = mes_text,
        mesTime = mes_time
    )

fun MessageDto.toMessageEntity(): MessageEntity =
    MessageEntity(
        messageId = message_id,
        idSender = id_sender,
        idChat = id_chat,
        content = content,
        time = time,
        type = type
    )

fun UserForChooseDto.toUserForChooseEntity(): UserForChooseEntity =
    UserForChooseEntity(
        id = id,
        foto = foto,
        nickname = nickname
    )