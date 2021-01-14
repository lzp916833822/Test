package com.eloam.process.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.eloam.process.data.DataRepository
import com.eloam.process.data.entity.DataInfo
import org.lico.core.base.BaseViewModel

class ViewWorkLogViewModel(
    private val context: Context,
    private val app: Application,
    private val dataRepository: DataRepository
) : BaseViewModel(app) {


    //live data use case

    fun letViewWorkLogObservable(): LiveData<PagingData<DataInfo>>  {
        return dataRepository.letViewWorkLogObservable().cachedIn(viewModelScope)
    }
}