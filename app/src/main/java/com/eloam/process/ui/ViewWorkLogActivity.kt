package com.eloam.process.ui

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.eloam.process.R
import com.eloam.process.adpter.LoaderBottomAdapter
import com.eloam.process.adpter.ViewWorkLogAdapter
import com.eloam.process.viewmodels.ViewWorkLogViewModel
import kotlinx.android.synthetic.main.activity_upload_file.*
import kotlinx.android.synthetic.main.top_layout.*
import kotlinx.android.synthetic.main.upload_file_item.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.lico.core.base.BaseActivity

class ViewWorkLogActivity : BaseActivity() {
    companion object {
        const val TAG = "ViewWorkLogActivity"
    }

    private val viewWorkLogViewModel: ViewWorkLogViewModel by viewModel()
    private lateinit var mViewWorkLogAdapter: ViewWorkLogAdapter
    private lateinit var mLoaderBottomAdapter: LoaderBottomAdapter
    override fun layoutId(): Int {
        return R.layout.activity_view_work_log
    }

    override fun initData() {
        uploadRv.layoutManager = LinearLayoutManager(this)
        mViewWorkLogAdapter = ViewWorkLogAdapter { onVisibility() }
        mLoaderBottomAdapter = LoaderBottomAdapter { mViewWorkLogAdapter.retry() }
        uploadRv.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        uploadRv.adapter = mViewWorkLogAdapter.withLoadStateFooter(mLoaderBottomAdapter)
    }


    override fun initView() {
        setView()
        getViewWorkLogInfo()

    }


    private fun getViewWorkLogInfo() {
        viewWorkLogViewModel.letViewWorkLogObservable().observe(this, Observer {
            lifecycleScope.launch {
                mViewWorkLogAdapter.submitData(it)

            }
        })

    }

    private fun onVisibility() {
        if (noDataIv.visibility == View.VISIBLE) {
            noDataIv.visibility = View.GONE
            noDataTv.visibility = View.GONE
        }
    }

    private fun setView() {
        checkBox.visibility = View.GONE
        centerTv.text = getString(R.string.view_test_records)
        backIv.setImageResource(R.drawable.ic_back)
        operateTv.text = getString(R.string.operate)
        operateTv.setTextColor(this.resources.getColor(R.color.black))
        backIv.setOnClickListener { finish() }
        noDataIv.setOnClickListener {
            mViewWorkLogAdapter.refresh()
        }
    }
}