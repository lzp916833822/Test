package org.lico.core.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

/**
 * @author: lzp
 * @Desc:
 */
abstract class BaseActivity : AppCompatActivity() {
    lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        mContext = this
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        initData()
        initView()
    }


    @LayoutRes
    protected abstract fun layoutId(): Int
    protected abstract fun initData()
    protected abstract fun initView()


    protected fun readyGo(clazz: Class<*>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
    }

    fun backClick(view: View) {
        this.finish()
    }



}