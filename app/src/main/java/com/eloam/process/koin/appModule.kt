package com.eloam.process.koin

import com.eloam.process.data.DataRepository
import com.eloam.process.data.RetrofitClient
import com.eloam.process.data.services.ApiService
import com.eloam.process.viewmodels.MainViewModel
import com.eloam.process.viewmodels.WelcomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * @author: lico
 * @create：2020/5/21
 * @describe：
 */
val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { WelcomeViewModel(get(), get(), get()) }
}


val repositoryModule = module {
    single { RetrofitClient.createRetrofit().create(ApiService::class.java) }
    single { DataRepository(get()) }
}


val appModule = listOf(viewModelModule, repositoryModule)