package com.eloam.process.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.eloam.process.data.DataRepository
import com.eloam.process.data.entity.MyLogInfo
import com.eloam.process.data.entity.UploadStatueResult
import com.eloam.process.utils.GoSonUtils
import com.eloam.process.utils.LogUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.lico.core.base.BaseViewModel
import java.io.File

class UploadViewModel(
    private val context: Context,
    private val app: Application,
    private val dataRepository: DataRepository
) : BaseViewModel(app) {

    companion object {
        const val TAG = "UploadViewModel"
    }

    var mRemoveId = mutableListOf<Int>()

    //上传结果
    var uploadingFileResult: MutableLiveData<UploadStatueResult> = MutableLiveData()

    fun  uploadTestFiles(myLogInfo: MyLogInfo, index: Int) {
        val filePath = myLogInfo.filePath
        val file = File(filePath)
        if (file.exists()) {
            val body: RequestBody = requestBody(myLogInfo, file, requestBody(file))
            launchOnlyresult({
                dataRepository.uploadTestFiles(body)
            }, {
                uploadingFileResult.postValue(UploadStatueResult(myLogInfo.id,index,0))
                mRemoveId.add(index)
            }, {
                uploadingFileResult.postValue(UploadStatueResult(myLogInfo.id,index,1))
                LogUtils.i(TAG, it.errMsg, 0, 0)
            }, {}, true)
        }


    }

    private fun requestBody(
        myLogInfo: MyLogInfo,
        file: File,
        fileRQ: RequestBody
    ): RequestBody {
        LogUtils.i(MainViewModel.TAG, GoSonUtils.toJson(myLogInfo), 0, 0)
        return MultipartBody.Builder().apply {
            addFormDataPart("mac", myLogInfo.mac)
            addFormDataPart("sn", myLogInfo.sn)
            addFormDataPart("reportTime", myLogInfo.reportTime.toString())
            addFormDataPart("employeeNo", myLogInfo.employeeNo)
            addFormDataPart("itemName", myLogInfo.itemName)
            addFormDataPart("terminalInfo", myLogInfo.terminalInfo)
            addFormDataPart("file", file.name, fileRQ)
        }.build()//可以任意调用该对象的任意方法，并返回该对象

    }

    private fun requestBody(file: File): RequestBody {
        return RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
    }

}