package com.eloam.process.data

import com.eloam.process.data.services.ApiService
import okhttp3.RequestBody
import org.lico.core.base.BaseModel

/**
 * @author: lico
 * @create：2020/5/25
 * @describe：
 */
class DataRepository(private val apiService: ApiService) : BaseModel() {

      suspend fun postVisitorInfo(body: RequestBody) = apiService.postVisitor(body)

      suspend fun getAdData(body: RequestBody) = apiService.getAdData(body)

      suspend fun get(body: RequestBody) = apiService.get(body)
}