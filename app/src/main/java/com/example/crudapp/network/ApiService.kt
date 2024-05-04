package com.example.crudapp.network

import com.example.crudapp.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // Define the base URL of your Flask app
    companion object {
        const val BASE_URL = "http://192.168.100.23:5000"
        const val AUTH_HEADER = "Authorization"
    }

    // Define the endpoints

    @POST("register")
    fun registerUser(@Body userData: RegisterModel): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("login")
    fun loginUser(@Body loginModel: Map<String, String>): Call<Map<String, String>>


    @POST("forgot_password")
    fun forgotPassword(@Body emailData: Map<String, String>): Call<Void>

    @POST("reset_password")
    fun resetPassword(@Body resetData: Map<String, String?>): Call<Void>

    @PUT("update_profile")
    fun updateProfile(@Header("Authorization") token: String, @Body profileData: UserInformation): Call<Void>

    @GET("users")
    fun getAllUsers(): Call<List<UserInformation>>

    @GET("users")
    fun getUser(@Header("Authorization") token: String?): Call<GetUserResponse>

    @POST("users")
    fun createUser(@Body userData: UserInformation): Call<Void>

    @DELETE("users")
    fun deleteUser(@Header("Authorization") authorization: String): Call<Void>
}
