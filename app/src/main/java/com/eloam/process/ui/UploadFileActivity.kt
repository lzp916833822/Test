package com.eloam.process.ui

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.eloam.process.R
import com.eloam.process.adpter.UploadFileInfoAdapter
import com.eloam.process.data.ObjectBox
import com.eloam.process.data.entity.MyLogInfo
import com.eloam.process.dialog.SweetAlertDialog
import com.eloam.process.utils.JUtils
import com.eloam.process.utils.LogUtils
import com.eloam.process.utils.NetUtils
import com.eloam.process.viewmodels.UploadViewModel
import com.serenegiant.utils.UIThreadHelper
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import kotlinx.android.synthetic.main.activity_upload_file.*
import kotlinx.android.synthetic.main.top_layout.*
import kotlinx.android.synthetic.main.upload_file_item.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.lico.core.base.BaseActivity
import java.io.File

class UploadFileActivity : BaseActivity() {
    companion object {
        const val TAG = "UploadFileActivity"
    }

    private var mSweetAlertDialog: SweetAlertDialog? = null
    private var mIndex = 0//总执行的数量

    private val uploadViewModel: UploadViewModel by viewModel()
    private var adInfoBox: Box<MyLogInfo> = ObjectBox.boxStore.boxFor()
    private var mUploadFileInfoAdapter: UploadFileInfoAdapter? = null
    override fun layoutId(): Int {
        return R.layout.activity_upload_file
    }

    override fun initData() {
        uploadRv.layoutManager = LinearLayoutManager(this)
        mUploadFileInfoAdapter = UploadFileInfoAdapter(this)
        uploadRv.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        uploadRv.adapter = mUploadFileInfoAdapter
        lifecycleScope.launch(IO) {
            val all = adInfoBox.all
            delay(200)
            runOnUiThread {
                onVisibility(mUploadFileInfoAdapter!!, all)
            }
        }
    }

    private fun onVisibility(
        adapter: UploadFileInfoAdapter,
        allData: MutableList<MyLogInfo>
    ) {
        runOnUiThread {
            allData.forEach {
                adapter.items.add(0, it)
            }
            isVisibility()
        }
    }

    override fun initView() {
        setView()
        onClick()
        onObserve()
    }

    private fun onObserve() {
        uploadViewModel.uploadingFileResult.observe(this, Observer {
            LogUtils.i(TAG, "it=$it ", 0, 0)
            removeAdapterData()
        })
    }

    /**
     * 上传成功，移除Adapter数据
     */
    private fun removeAdapterData() {
        if (mIndex == uploadViewModel.mPosition) {
            mIndex = 0
            uploadViewModel.mPosition = 0
            mSweetAlertDialog?.dismiss()
            removeOrDeleteBoxData()
            uploadViewModel.mRemoveData.clear()
            isVisibility()
        }
    }

    /**
     * 上传成功删除数据库数据和移除列表数据和删除文件
     */
    private fun removeOrDeleteBoxData() {
        uploadViewModel.mRemoveData.forEach {
            val remove = adInfoBox.remove(it.id)
            val removeAdapter = mUploadFileInfoAdapter!!.items.remove(it)
            val file = File(it.filePath)
            if (file.exists()) {
                val delete = file.delete()
                LogUtils.i(TAG, "delete=$delete ", 0, 0)
            }
            LogUtils.i(TAG, "remove=$remove  id=${it.id}removeAdapter=$removeAdapter ", 0, 0)
        }
    }

    private fun isVisibility() {
        if (mUploadFileInfoAdapter?.items.isNullOrEmpty()) {
            noDataIv.visibility = View.VISIBLE
            noDataTv.visibility = View.VISIBLE
        } else {
            noDataIv.visibility = View.GONE
            noDataTv.visibility = View.GONE
        }
    }

    private fun onClick() {
        backIv.setOnClickListener {
            finish()
        }

        rightTv.setOnClickListener {
            if (NetUtils.isNetworkAvailable(this)) {
                uploadData()
            } else {
                JUtils.onToastLong(R.string.net_error)
            }

        }

        checkBox.setOnClickListener {
            mUploadFileInfoAdapter!!.items.forEach {
                val myLogInfo = it as MyLogInfo
                myLogInfo.isCheck = checkBox.isChecked
                LogUtils.d(TAG, "isCheck=${myLogInfo.isCheck}  id=${it.id}", 0, 0)
            }
            mUploadFileInfoAdapter!!.notifyDataSetChanged()
        }
    }


    /**
     * 遍历所有数据，需要上传的上传
     */
    private fun uploadData() {
        mUploadFileInfoAdapter!!.items.forEachIndexed { index, info ->
            val myLogInfo = info as MyLogInfo
            if (myLogInfo.isCheck) {
                if (File(myLogInfo.filePath).exists()) {
                    showDialog(getString(R.string.loading_waite))
                    mIndex++
                    uploadViewModel.uploadTestFiles(myLogInfo, mIndex)
                } else {
                    uploadViewModel.mRemoveData.add(0, myLogInfo)
                }

            }
            LogUtils.i(TAG, "isCheck=${myLogInfo.isCheck}  mIndex=$mIndex index=$index", 0, 0)

        }


    }


    private fun showDialog(hintText: String) {
        UIThreadHelper.runOnUiThread {
            if (mSweetAlertDialog == null) {
                mSweetAlertDialog = SweetAlertDialog(mContext)
            }
            mSweetAlertDialog?.setView(SweetAlertDialog.NORMAL_TYPE, hintText)
            mSweetAlertDialog?.setCanceledOnTouchOutside(false)
            if (!mSweetAlertDialog!!.isShowing) {
                mSweetAlertDialog?.show()
            }

        }

    }

    private fun setView() {
        centerTv.text = getString(R.string.uploading_test_records)
        backIv.setImageResource(R.drawable.ic_back)
        rightTv.visibility = View.VISIBLE
        rightTv.text = getString(R.string.upload_text)
        operateTv.text = getString(R.string.operate)
        operateTv.setTextColor(this.resources.getColor(R.color.black))
    }
}