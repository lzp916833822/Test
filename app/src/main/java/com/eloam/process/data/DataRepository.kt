package com.eloam.process.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.eloam.process.data.entity.DataInfo
import com.eloam.process.service.ApiService
import okhttp3.RequestBody
import org.lico.core.base.BaseModel

/**
 * @author: lzp
 * @create：2020/12/25
 * @describe：
 */
class DataRepository(private val apiService: ApiService) : BaseModel() {
    companion object {
        const val DEFAULT_PAGE_INDEX = 1
        const val DEFAULT_PAGE_SIZE = 20

    }


    fun letViewWorkLogObservable(pagingConfig: PagingConfig = getDefaultPageConfig()): LiveData<PagingData<DataInfo>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { ViewWorkLogPagingSource(apiService) }
        ).liveData
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = DEFAULT_PAGE_SIZE, enablePlaceholders = true)
    }

    suspend fun findReportTerminalLog(body: RequestBody) = apiService.findReportTerminalLog(body)

    suspend fun uploadTestFiles(body: RequestBody) = apiService.uploadTestFiles(body)
}