package com.eloam.process.data

import com.eloam.process.service.ApiService
import okhttp3.RequestBody
import org.lico.core.base.BaseModel

/**
 * @author: lico
 * @create：2020/5/25
 * @describe：
 */
class DataRepository(private val apiService: ApiService) : BaseModel() {


    suspend fun findReportTerminalLog(body: RequestBody) = apiService.findReportTerminalLog(body)

    suspend fun uploadTestFiles(body: RequestBody) = apiService.uploadTestFiles(body)
}