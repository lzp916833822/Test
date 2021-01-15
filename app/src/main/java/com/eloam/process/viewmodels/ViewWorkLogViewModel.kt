package com.eloam.process.viewmodels

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.eloam.process.MyApp
import com.eloam.process.data.DataRepository
import com.eloam.process.data.entity.DataInfo
import com.eloam.process.data.entity.PostRequestBody
import com.eloam.process.utils.GoSonUtils
import com.eloam.process.utils.LogUtils
import com.eloam.process.utils.Uuid
import org.lico.core.base.BaseViewModel

class ViewWorkLogViewModel(
    private val context: Context,
    private val app: Application,
    private val dataRepository: DataRepository
) : BaseViewModel(app) {


    //live data use case

    fun letViewWorkLogObservable(): LiveData<PagingData<DataInfo>> {
        return dataRepository.letViewWorkLogObservable().cachedIn(viewModelScope)
    }


    fun getLogInfo(page: Int, size: Int) {
        val body =
            PostRequestBody(page, size, Build.SERIAL, Uuid.getUUID(MyApp.getApplication())!!)

        launchOnlyresult({
            dataRepository.findReportTerminalLog(getRequestBody(GoSonUtils.toJson(body)))
        }, {

        }, {
            LogUtils.i(UploadViewModel.TAG, it.errMsg, 0, 0)
        }, {}, true)

    }
}