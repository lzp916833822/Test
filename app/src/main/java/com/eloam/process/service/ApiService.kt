package com.eloam.process.service


import com.eloam.process.data.entity.GetTestLogInfo
import com.eloam.process.data.entity.ResultInfo
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * @author: lzp
 * @create：2020/5/25
 * @describe：
 */
interface ApiService {

    companion object {

        //上传测试日志
        const val UPLOADING_TEST_LOGS =
            "https://pmp.eloam.net/api/logs/reportTestLogsFromTerminal"

        //查询测试日志参数
        const val FIND_TERMINAL_TEST_LOG = "https://pmp.eloam.net/api/logs/findTerminalTestLog"

    }


    @POST(FIND_TERMINAL_TEST_LOG)
    suspend fun findReportTerminalLog(@Body request: RequestBody): Response<GetTestLogInfo> //查询测试日志参数


    @POST(UPLOADING_TEST_LOGS)
    suspend fun uploadTestFiles(@Body body: RequestBody?): Response<ResultInfo>//上传测试日志
}