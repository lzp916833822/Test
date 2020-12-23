package com.eloam.process.viewmodels

import android.app.Application
import android.content.Context
import androidx.appcompat.widget.AppCompatCheckBox
import com.eloam.process.data.DataRepository
import org.lico.core.base.BaseViewModel

/**
 * @author: lico
 * @create：2020/5/21
 * @describe：
 */
class WelcomeViewModel(
    private val context: Context,
    private val app: Application,
    private val dataRepository: DataRepository
) : BaseViewModel(app) {


    var listCheckBox = mutableListOf<AppCompatCheckBox>()




}