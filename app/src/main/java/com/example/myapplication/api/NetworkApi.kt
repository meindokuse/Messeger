package com.example.myapplication.api


import com.example.myapplication.chats.MessageInChat
import com.example.myapplication.elements.ItemChat
import com.example.myapplication.elements.ProfileInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path




interface networkApi {
    @POST("/auth/register")
    fun registerUser(@Body request: RegistrationRequest): Call<ProfileInfo>

    @GET("/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<ProfileInfo>

    @GET("profile/{id_profile}")
    fun getProfile(@Path("id_profile") idProfile: String):Call<ProfileInfo>

    @GET("chats/{id_profile}/")
    fun getChats(@Path("id_profile") idProfile: String):Call<List<ItemChat>>

    @GET("messages/{id_chat}/{id_user}")
    fun getMessendges(
        @Path("id_chat") chatId:String,
        @Path("id_user") idUser:String
    ):Call<List<MessageInChat>>

    @POST("profile/{id_profile}")
    fun changeProfileInfo(@Path("id_profile") idProfile:String):Call<ProfileInfo>


    data class LoginRequest(val login:String,val password:String)

    data class RegistrationRequest(
        val profileInfo: ProfileInfo,
        val login:String,
        val password: String)



}