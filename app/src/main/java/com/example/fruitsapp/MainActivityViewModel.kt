package com.example.fruitsapp

import android.graphics.Bitmap
import android.util.LruCache
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityViewModel : ViewModel() {
    private var recycleViewLiveData: MutableLiveData<FruitsListModel> = MutableLiveData()
    private var mMemoryCache: LruCache<String, Bitmap>? = null
    private var errorLiveData = MutableLiveData<String?>()

    fun getRecyclerListObserver(callToApi:Boolean): MutableLiveData<FruitsListModel> {
        if (callToApi){
            apiCall()
        }
        return recycleViewLiveData;
    }

    fun getErrorLiveData(): MutableLiveData<String?> {
        return errorLiveData
    }

    private fun apiCall() {
        viewModelScope.launch(Dispatchers.IO) {
            val retroInstance = RetroInstance.getRetroInstance()?.create(RetroService::class.java)
            retroInstance?.getFruitsListFromApi()?.enqueue(FruitCallback())
        }
    }

    inner class FruitCallback : Callback<JsonObject> {
        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
            if (response.isSuccessful) {
                response.body()?.let {
                    try {
                        val fruitString = it.toString()
                        val gson = Gson()
                        val fruitData: FruitsListModel =
                            gson.fromJson(fruitString, FruitsListModel::class.java)
                        recycleViewLiveData.postValue(fruitData)
                    } catch (e: JsonParseException) {
                        onFail(e.message)
                    }
                }
            } else {
                onFail(null)
            }
        }

        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            onFail(t.message)
        }
    }

    private fun onFail(message: String?) {
        errorLiveData.postValue(message)
    }

    fun setMemoryCache(cache: LruCache<String, Bitmap>?) {
        this.mMemoryCache = cache
    }
    fun getBitmapFromMemCache(key: String?): Bitmap? {
        return mMemoryCache?.get(key)
    }

    fun addBitmapToMemoryCache(key: String?, bitmap: Bitmap?) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache?.put(key, bitmap)
        }
    }


}


