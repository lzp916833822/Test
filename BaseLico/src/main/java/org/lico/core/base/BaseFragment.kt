package org.lico.core.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * @author: lico
 * @Desc:
 */
abstract class BaseFragment : Fragment() {

    lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mContext = activity as Context
        initData()
        super.onViewCreated(view, savedInstanceState)
    }

    protected fun readyGo(clazz: Class<*>) {
        var intent = Intent(activity, clazz)
        startActivity(intent)
    }

    @LayoutRes
    protected abstract fun layoutId(): Int
    protected abstract fun initData()
}