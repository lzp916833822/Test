package com.eloam.process.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.eloam.process.data.DataRepository
import com.eloam.process.data.ObjectBox
import com.eloam.process.data.RetrofitClient
import com.eloam.process.data.entity.AdInfo
import com.eloam.process.data.entity.AdPost
import com.eloam.process.data.services.ApiService
import com.google.gson.Gson
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

/**
 * @author: lico
 * @create：2020/6/4
 * @describe：
 */
class AdService : Service(){
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private lateinit var adInfoBox: Box<AdInfo>

    override fun onCreate() {
        super.onCreate()
        val mainScope = MainScope()
        mainScope.launch(Dispatchers.IO) {
            while (true){
                adInfoBox = ObjectBox.boxStore.boxFor()
                val apiService = RetrofitClient.createRetrofit().create(ApiService::class.java)
                val dataRepository = DataRepository(apiService)

                val gson = Gson()
                val adPost = AdPost("E06165932000000000000000", 0, 15)
                val jsonStr = gson.toJson(adPost)
                val body =
                    RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonStr)
                try {
                    var bb = dataRepository.getAdData(body)
                    adInfoBox.removeAll()
                    val adInfo = bb.body()
                    adInfoBox.put(adInfo)
                }catch (e: Exception){
                    e.printStackTrace()
                }
               delay(3600000L)
            }
        }
    }
}