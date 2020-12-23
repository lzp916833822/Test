package org.lico.core.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.SharedPreferencesCompat


/**
 * @author: lico
 * @Desc:
 */
object SharedPreUtil {

    var FILLNAME = "config"

    /**
     * 存入某个key对应的value值
     *
     * @param context
     * @param key
     * @param value
     */
    fun put(context: Context, key: String?, value: Any?) {
        val sp: SharedPreferences = context.getSharedPreferences(FILLNAME, Context.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sp.edit()
        if (value is String) {
            edit.putString(key, value as String?)
        } else if (value is Int) {
            edit.putInt(key, value as Int)
        } else if (value is Boolean) {
            edit.putBoolean(key, value as Boolean)
        } else if (value is Float) {
            edit.putFloat(key, value as Float)
        } else if (value is Long) {
            edit.putLong(key, value as Long)
        }
        SharedPreferencesCompat.EditorCompat.getInstance().apply(edit)
    }

    /**
     * 得到某个key对应的值
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    operator fun get(context: Context, key: String, defValue: Any): Any? {
        val sp: SharedPreferences = context.getSharedPreferences(FILLNAME, Context.MODE_PRIVATE)
        if (defValue is String) {
            return sp.getString(key, defValue as String)
        } else if (defValue is Int) {
            return sp.getInt(key, (defValue as Int))
        } else if (defValue is Boolean) {
            return sp.getBoolean(key, (defValue as Boolean))
        } else if (defValue is Float) {
            return sp.getFloat(key, (defValue as Float))
        } else if (defValue is Long) {
            return sp.getLong(key, (defValue as Long))
        }
        return null
    }

    /**
     * 返回所有数据
     *
     * @param context
     * @return
     */
    fun getAll(context: Context): Map<String?, *>? {
        val sp: SharedPreferences = context.getSharedPreferences(FILLNAME, Context.MODE_PRIVATE)
        return sp.all
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    fun remove(context: Context, key: String?) {
        val sp: SharedPreferences = context.getSharedPreferences(FILLNAME, Context.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sp.edit()
        edit.remove(key)
        SharedPreferencesCompat.EditorCompat.getInstance().apply(edit)
    }

    /**
     * 清除所有内容
     *
     * @param context
     */
    fun clear(context: Context) {
        val sp: SharedPreferences = context.getSharedPreferences(FILLNAME, Context.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sp.edit()
        edit.clear()
        SharedPreferencesCompat.EditorCompat.getInstance().apply(edit)
    }
}