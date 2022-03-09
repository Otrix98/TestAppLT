package com.example.testapplt.networking
import com.example.testapplt.data.models.AuthStatus
import com.example.testapplt.data.models.ListItem
import com.example.testapplt.data.models.PhoneMask
import retrofit2.http.*

interface DevExamApi {

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("auth")
    suspend fun checkAuth(@Field("phone") phone : String, @Field("password") password : String): AuthStatus

    @GET("phone_masks")
    suspend fun getPhoneMask(): PhoneMask

    @GET("posts")
    suspend fun getList(): List<ListItem>
}

