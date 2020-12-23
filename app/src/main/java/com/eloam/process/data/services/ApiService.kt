package com.eloam.process.data.services

import com.eloam.process.data.entity.AdInfo
import com.eloam.process.data.entity.InfoResult
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * @author: lico
 * @create：2020/5/25
 * @describe：
 */
interface ApiService {
    @POST("http://47.105.220.27:8080/ylty/api/rzhy/saveRzry.do")
    suspend fun postVisitor(@Body body: RequestBody): Response<InfoResult>

    @POST("http://zhuopai.shandongchiyuan.com/a/open/ad/list")
    suspend fun getAdData(@Body body: RequestBody): Response<AdInfo>

    @GET
    suspend fun get(@Body body: RequestBody): Response<Any>
}