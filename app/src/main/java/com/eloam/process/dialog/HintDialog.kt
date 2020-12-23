package com.eloam.process.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import com.eloam.process.R
import com.face.sweepplus.data.`interface`.HintDialogBtnListener
import kotlinx.android.synthetic.main.hint_dialog.*

class HintDialog constructor(
    context: Context?,
    listener: HintDialogBtnListener
) : Dialog(context!!, R.style.alert_dialog) {


    private var mContext: Context? = context
    private lateinit var mInputEdt: AppCompatEditText
    private lateinit var mCancelTv: TextView
    private lateinit var mEnterTv: TextView
    private lateinit var mTitleTv: TextView
    private lateinit var mRestoreHintTv: TextView


    private fun intiView() {
        mCancelTv = cancelTv
        mEnterTv = enterTv
        mTitleTv = titleTv
        mRestoreHintTv = restoreHintTv
        mInputEdt = inputEdt

    }

    companion object {
        const val NORMAL_TYPE = 0//是否恢复出厂设置
        const val ONE_TYPE = 1//设置key
        const val TWO_TYPE = 2// 设置url

    }

    init {
        setContentView(R.layout.hint_dialog)
        setCancelable(true)
        setCanceledOnTouchOutside(false)
        intiView()
        enterTv.setOnClickListener {
            dismiss()
            listener.enter(mInputEdt.text.toString())
        }
        cancelTv.setOnClickListener {
            dismiss()
            listener.cancel()

        }
    }


    private fun setTextOrVisibility(type: Int, string: String) {

        when (type) {
            NORMAL_TYPE -> {
                mInputEdt.visibility = View.GONE
                mTitleTv.visibility = View.GONE
                mRestoreHintTv.visibility = View.VISIBLE

            }

            ONE_TYPE -> {
                mTitleTv.text = mContext?.getString(R.string.whether_to_restore_factory_configuration)
                mInputEdt.setText(string)
            }

            TWO_TYPE -> {
                mTitleTv.text = mContext?.getString(R.string.url)
                mInputEdt.setText(string)
            }

            else -> {

            }


        }

    }


    fun setCanceledOnTouch(isCanceled: Boolean) {
        setCanceledOnTouchOutside(isCanceled)
    }


    fun setView(type: Int, string: String) {
        setTextOrVisibility(type, string)
        mRestoreHintTv.text = string
    }


}