package com.eloam.process.data

import androidx.paging.PagingSource
import com.eloam.process.MyApp
import com.eloam.process.data.DataRepository.Companion.DEFAULT_PAGE_INDEX
import com.eloam.process.data.entity.DataInfo
import com.eloam.process.data.entity.PostRequestBody
import com.eloam.process.service.ApiService
import com.eloam.process.utils.GoSonUtils
import com.eloam.process.utils.Uuid
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException

/**
 * provides the data source for paging lib from api calls
 */

class ViewWorkLogPagingSource(val apiService: ApiService) :
    PagingSource<Int, DataInfo>() {

    private fun getRequestBody(body: String): RequestBody {
        return RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), body)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataInfo> {
        //for first case it will be null, then we can pass some default value, in our case it's 1
        val page = params.key ?: DEFAULT_PAGE_INDEX
        val postRequestBody =
            PostRequestBody(page, params.loadSize, "", Uuid.getUUID(MyApp.getApplication())!!)
        val toJson = GoSonUtils.toJson(postRequestBody)
        return try {
            val response = apiService.findReportTerminalLog(getRequestBody(toJson))
            val list = response.body()!!.data.list
            LoadResult.Page(
                list,
                if (page == DEFAULT_PAGE_INDEX) null else page - 1,
                if (list.isEmpty()) null else page + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

}