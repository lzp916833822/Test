package com.eloam.process.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.eloam.process.R
import kotlinx.android.synthetic.main.activity_views_log_web_view.*
import kotlinx.android.synthetic.main.top_layout.*
import org.lico.core.base.BaseActivity


class ViewsLogWebViewActivity : BaseActivity() {
    companion object {
        const val TAG = "ViewsLogWebViewActivity"
        private const val LOG_NAME = "log_name"
        private const val LOG_OPEN_URL = "log_open_url"

        fun onStartActivity(context: Context, logName: String, logUrl: String) {
            val intent = Intent(context, ViewsLogWebViewActivity::class.java)
            intent.putExtra(LOG_NAME, logName)
            intent.putExtra(LOG_OPEN_URL, logUrl)
            context.startActivity(intent)

        }
    }

    private lateinit var mLogName: String
    private lateinit var mLogUrl: String

    override fun layoutId(): Int {
        return R.layout.activity_views_log_web_view
    }

    override fun initData() {
        mLogName = intent.getStringExtra(LOG_NAME)
        mLogUrl = intent.getStringExtra(LOG_OPEN_URL)
    }

    override fun initView() {
        setView()
        setWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView() {
        val settings = webView.settings
        // 设置WebView支持JavaScript
        settings.javaScriptEnabled = true
        //支持自动适配
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.setSupportZoom(true) //支持放大缩小

        settings.builtInZoomControls = true //显示缩放按钮

        settings.blockNetworkImage = true // 把图片加载放在最后来加载渲染

        settings.domStorageEnabled = true //开启DOM形式存储
        settings.databaseEnabled = true //开启数据库形式存储
        val appCacheDir =
            this.applicationContext.getDir("cache", Context.MODE_PRIVATE)
                .path //缓存数据的存储地址
        settings.setAppCachePath(appCacheDir)
        settings.setAppCacheEnabled(true) //开启缓存功能
        settings.cacheMode = WebSettings.LOAD_DEFAULT //缓存模式
        settings.allowFileAccess = true
        settings.domStorageEnabled = true
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

        //设置不让其跳转浏览器
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }
        }

        webView.webChromeClient = WebChromeClient()
        webView.loadUrl(mLogUrl)
    }

    private fun setView() {
        centerTv.text = mLogName
        backIv.setImageResource(R.drawable.ic_back)
        backIv.setOnClickListener { finish() }
    }
}