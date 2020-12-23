package com.eloam.process.ui

import android.os.Process
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import com.eloam.process.R
import com.eloam.process.dialog.SweetAlertDialog
import com.eloam.process.utils.LogUtils
import com.eloam.process.utils.StorageUtils
import com.eloam.process.viewmodels.WelcomeViewModel
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.serenegiant.utils.UIThreadHelper
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.top_layout.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.lico.core.base.BaseActivity
import java.util.*
import kotlin.system.exitProcess

/**
 * @author: lico
 * @create：2020/6/3
 * @describe：
 */
class WelcomeActivity : BaseActivity() {
    companion object {
        const val TAG = "WelcomeActivity"
    }


    override fun layoutId() = R.layout.activity_welcome
    private val welcomeViewModel: WelcomeViewModel by viewModel()
    private var allIsCheck = false
    private var number = 0
    override fun initData() {
        getPermission()
        initView()
        onClickListener()
        storageHint()
    }

    /**
     * 内存超过80%提醒和SD卡不存在提醒
     */
    private fun storageHint() {
        val type = StorageUtils.readSDCard(this, true)
        LogUtils.d(TAG, "storageHint$type")
        if (type == 1) {
            showDialog(getTextString(type))
        }
    }

    private fun showDialog(hintText: String) {
        UIThreadHelper.runOnUiThread {

            val sd = SweetAlertDialog(mContext)
            sd.setView(SweetAlertDialog.MEMORY_HINT_TYPE, hintText)
            if (!sd.isShowing)
                sd.show()

        }

    }

    private fun getTextString(type: Int): String {
        return if (type == -2) getString(R.string.please_insert_sd_hint) else getString(R.string.SD_Card_has_reached_hint)
    }

    private fun onClickListener() {
        backIv.setOnClickListener {
            exit()
        }
        allBox.setOnClickListener {
            isCheck(allBox.isChecked)
            setEnabled()
        }

        mainCameraBox.setOnClickListener {
            allBox.isChecked = getIsCheck()
            setEnabled()
        }

        binocularColorBox.setOnClickListener {
            allBox.isChecked = getIsCheck()
            setEnabled()
        }

        binocularBlackBox.setOnClickListener {
            allBox.isChecked = getIsCheck()
            setEnabled()
        }

        qrCodeBox.setOnClickListener {
            allBox.isChecked = getIsCheck()
            setEnabled()
        }

        idCardBox.setOnClickListener {
            allBox.isChecked = getIsCheck()
            setEnabled()
        }

        fingerprintsBox.setOnClickListener {
            allBox.isChecked = getIsCheck()
            setEnabled()
        }

        icBox.setOnClickListener {
            allBox.isChecked = getIsCheck()
            setEnabled()
        }

        icBox.setOnClickListener {
            allBox.isChecked = getIsCheck()
            setEnabled()
        }
        startTv.setOnClickListener {

            MainActivity.startIntent(this, getIsCheckCode() as ArrayList<Int>, number)
        }
        addTextListener()

        TestBtn.setOnClickListener {


        }
    }

    private fun addTextListener() {
        numberEdt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && !TextUtils.isEmpty(s.toString())) {
                    number = s.toString().toInt()
                    startTv.isEnabled = number in 100..1000000 && allIsCheck
                } else {
                    startTv.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    /**
     * 设置开启按钮是否可以点击
     */
    private fun setEnabled() {
        allIsCheck = getAllIsCheck()
        startTv.isEnabled = number in 100..100000 && allIsCheck
    }

    /**
     * 获取选中项
     */
    private fun getIsCheckCode(): MutableList<Int> {
        val listCode = mutableListOf<Int>()
        for ((index, value) in welcomeViewModel.listCheckBox.withIndex()) {
            if (value.isChecked) {
                listCode.add(index)
            }
        }
        return listCode
    }

    /**
     * 获取是否没有选中
     */
    private fun getIsCheck(): Boolean {

        for (box in welcomeViewModel.listCheckBox) {
            if (!box.isChecked)
                return box.isChecked
        }
        return true
    }

    /**
     * 获取是否有选中
     */
    private fun getAllIsCheck(): Boolean {

        for (box in welcomeViewModel.listCheckBox) {
            if (box.isChecked)
                return box.isChecked
        }
        return false
    }

    private fun isCheck(isBox: Boolean) {

        for (box in welcomeViewModel.listCheckBox) {
            box.isChecked = isBox
        }
    }


    private fun initView() {
        welcomeViewModel.listCheckBox.add(mainCameraBox)
        welcomeViewModel.listCheckBox.add(binocularColorBox)
        welcomeViewModel.listCheckBox.add(binocularBlackBox)
        welcomeViewModel.listCheckBox.add(qrCodeBox)
        welcomeViewModel.listCheckBox.add(idCardBox)
        welcomeViewModel.listCheckBox.add(fingerprintsBox)
        welcomeViewModel.listCheckBox.add(icBox)


    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 退出程序
            exit()
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun exit() {
        Process.killProcess(Process.myPid())
        exitProcess(1)
    }
    private fun getPermission() {
        XXPermissions.with(this).permission(
            Permission.READ_EXTERNAL_STORAGE,
            Permission.WRITE_EXTERNAL_STORAGE
        ) //不指定权限则自动获取清单中的危险权限
            .request(object : OnPermission {
                override fun hasPermission(granted: List<String>, isAll: Boolean) {

                }

                override fun noPermission(denied: List<String>, quick: Boolean) {
                    finish()
                }
            })
    }




}
