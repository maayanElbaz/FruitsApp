package com.example.fruitsapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetroInstance {

    companion object{
        var retrofit : Retrofit? = null
        val BaseURL= "https://dev-api.com/fruitsBT/"
        fun getRetroInstance(): Retrofit? {
            if (retrofit == null){
                retrofit =   Retrofit.Builder()
                    .baseUrl(BaseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
    }
}