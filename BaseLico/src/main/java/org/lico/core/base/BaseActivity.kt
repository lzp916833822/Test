package org.lico.core.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.reflect.KProperty

/**
 * @author: lico
 * @Desc:
 */
abstract class BaseActivity : AppCompatActivity() {
    lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        mContext = this
        super.onCreate(savedInstanceState)
        setContentView(layoutId())

        initData()
    }


    @LayoutRes
    protected abstract fun layoutId(): Int
    protected abstract fun initData()


    protected fun readyGo(clazz: Class<*>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
    }

    fun backClick(view: View) {
        this.finish()
    }

    protected class IntegerArrayListExtra(private val key: String? = null) {
        private val KProperty<*>.extraName: String?
            get() = this@IntegerArrayListExtra.key ?: name

        operator fun getValue(intent: Intent, property: KProperty<*>): ArrayList<Int> =
            intent.getIntegerArrayListExtra(property.extraName)

        operator fun setValue(intent: Intent, property: KProperty<*>, value: ArrayList<Int>) {
            intent.putIntegerArrayListExtra(property.extraName, value)
        }
    }

    protected class IntegerExtra(private val key: String? = null) {
        private val KProperty<*>.extraName: String?
            get() = this@IntegerExtra.key ?: name

        operator fun getValue(intent: Intent, property: KProperty<*>): Int =
            intent.getIntExtra(property.extraName, 0)

        operator fun setValue(intent: Intent, property: KProperty<*>, value: Int) {
            intent.putExtra(property.extraName, value)
        }
    }
}