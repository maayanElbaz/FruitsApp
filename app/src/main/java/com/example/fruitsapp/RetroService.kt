package com.example.fruitsapp

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET

interface RetroService {

    @GET("getFruits")
    fun getFruitsListFromApi() : Call<JsonObject>

}