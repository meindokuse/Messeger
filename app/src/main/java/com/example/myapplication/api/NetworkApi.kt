package com.example.myapplication.api


import com.example.myapplication.models.MessageInChat
import com.example.myapplication.models.ProfileInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path




interface NetworkApi {
    @GET("/user/{user_id}")
    suspend fun getInfoUser(
        @Path("user_id") id:String
    ): Response<User>

    @POST("/user/")
    suspend fun postUser(
        @Body userdata: ProfileInfo
    ):Response<ApiResponse>

    @POST("/post_message")
    suspend fun postMessage(
        @Body message: MessageInChat
    ):Response<ApiResponse>

    @POST("/post_chat")
    suspend fun postChat(
        @Body message: DataForCreateChat
    ):Response<ApiResponse>

    @GET("/get_messages/{chat_id}")
    suspend fun getAllMessage(
        @Path("chat_id") chatId:String
    ):Response<MessageResponse>

    @GET("/open_chats/{user_id}")
    suspend fun getAllChats(
        @Path("user_id") userId:String
    ):Response<ChatResponse>

    data class ApiResponse(
        val message:String,
        val status:Int
    )

}