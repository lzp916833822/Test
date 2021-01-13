package com.eloam.process.ui

import com.eloam.process.R
import com.eloam.process.viewmodels.SettingViewModel
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.top_layout.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.lico.core.base.BaseActivity

class SettingActivity : BaseActivity() {


    private val settingViewModel: SettingViewModel by viewModel()

    override fun layoutId(): Int {
        return R.layout.activity_setting
    }

    override fun initData() {

    }

    override fun initView() {
        centerTv.text = getString(R.string.setting_text)
        backIv.setImageResource(R.drawable.ic_back)
        backIv.setOnClickListener {
            finish()
        }
        viewRecordsLayout.setOnClickListener {

        }
        uploadingLayout.setOnClickListener {

        }

    }
}