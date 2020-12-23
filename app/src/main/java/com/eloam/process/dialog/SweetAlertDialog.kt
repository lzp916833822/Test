package com.eloam.process.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.eloam.process.R
import com.hb.dialog.myDialog.ProgressWheel
import kotlinx.android.synthetic.main.alert_dialog.*

class SweetAlertDialog constructor(
    context: Context?
) : Dialog(context!!, R.style.alert_dialog) {


    private var mContext: Context? = context
    private lateinit var mHintIv: ImageView
    private lateinit var mHintTv: TextView
    private lateinit var mDownloadingTv: TextView
    private lateinit var mProgressWheel: ProgressWheel


    private fun intiView() {
        mHintIv = hintIv
        mHintTv = hintTv
        mDownloadingTv = downloadingTv
        mProgressWheel = progress_wheel
        hintIv.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val ABNORMAL_TYPE = -2//外Sd卡不存在提醒
        const val MEMORY_HINT_TYPE = -3//外Sd卡内存卡超过80%提醒
        const val NORMAL_TYPE = 0//等待
        const val DOWNLOADING_TYPE = 1//下载中
        const val RESULT_CODE_SUCCEED = 200    // 取件成功

    }

    init {
        setContentView(R.layout.alert_dialog)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        intiView()

    }


    private fun setVisibility(type: Int) {

        when (type) {
            NORMAL_TYPE -> {
                mProgressWheel.visibility = View.VISIBLE
                mHintIv.visibility = View.GONE
            }

            ABNORMAL_TYPE -> {
                mProgressWheel.visibility = View.GONE
                mHintIv.visibility = View.VISIBLE
            }

            MEMORY_HINT_TYPE -> {
                mProgressWheel.visibility = View.GONE
                mHintIv.visibility = View.VISIBLE
            }

            DOWNLOADING_TYPE -> {
                mProgressWheel.visibility = View.GONE
                mHintIv.visibility = View.GONE
                mDownloadingTv.visibility = View.VISIBLE

            }

            else -> {
                mProgressWheel.visibility = View.GONE
                mHintIv.visibility = View.VISIBLE

            }


        }

    }

    private fun getImageId(type: Int): Int {

        return when (type) {
            RESULT_CODE_SUCCEED ->
                R.mipmap.sf_correct_ic
            ABNORMAL_TYPE ->
                R.mipmap.sf_abnormal_hint_ic
            MEMORY_HINT_TYPE ->
                R.mipmap.sf_abnormal_hint_ic
            else ->
                R.mipmap.sf_error_ic
        }
    }



    private fun setImageResourceImg(type: Int) {
        mHintIv.setImageDrawable(mContext?.resources?.getDrawable(getImageId(type)))
    }

    private fun setTextViewText(hintText: String) {
        mHintTv.text = hintText
    }

    private fun setDownloadingTv(hintText: String) {
        mDownloadingTv.text = hintText
    }

    fun setView(type: Int, hintText: String, loading: String) {
        setDownloadingTv(loading)
        setTextViewText(hintText)
        setImageResourceImg(type)
        setVisibility(type)
    }

    fun setView(type: Int, hintText: String) {
        setTextViewText(hintText)
        setImageResourceImg(type)
        setVisibility(type)
    }

}