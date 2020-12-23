package com.eloam.process.utils

import android.R
import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.eloam.process.MyApp.Companion.getApplication
import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.regex.Matcher
import java.util.regex.Pattern

object JUtils {
    var TAG: String? = null
    var DEBUG = false

    //帐号长度
    const val mPhoneLength = 11

    //密码长度
    const val mTextLength = 20

    //验证码长度
    const val mCodeLength = 6
    fun onToast(text: String?) {
        android.widget.Toast.makeText(
            getApplication(),
            text,
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }

    fun onToast(stringId: Int) {
        android.widget.Toast.makeText(
            getApplication(),
            getApplication().getString(stringId),
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }

    fun onToastLong(text: String?) {
        android.widget.Toast.makeText(
            getApplication(),
            text,
            android.widget.Toast.LENGTH_LONG
        ).show()
    }

    fun onToastLong(id: Int) {
        android.widget.Toast.makeText(
            getApplication(),
            getApplication().getString(id),
            android.widget.Toast.LENGTH_LONG
        ).show()
    }

    /**
     * dp转px
     */
    fun dip2px(dpValue: Float): Int {
        val scale =
            getApplication().resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * px转dp
     */
    fun px2dip(pxValue: Float): Int {
        val scale =
            getApplication().resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 取屏幕宽度
     *
     * @return
     */
    val screenWidth: Int
        get() {
            val dm =
                getApplication().resources.displayMetrics
            return dm.widthPixels
        }

    /**
     * 取屏幕高度
     *
     * @return
     */
    val screenHeight: Int
        get() {
            val dm =
                getApplication().resources.displayMetrics
            return dm.heightPixels - statusBarHeight
        }

    /**
     * 取屏幕高度包含状态栏高度
     *
     * @return
     */
    val screenHeightWithStatusBar: Int
        get() {
            val dm =
                getApplication().resources.displayMetrics
            return dm.heightPixels
        }

    /**
     * 取导航栏高度
     *
     * @return
     */
    val navigationBarHeight: Int
        get() {
            var result = 0
            val resourceId = getApplication().resources
                .getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = getApplication().resources
                    .getDimensionPixelSize(resourceId)
            }
            return result
        }

    /**
     * 取状态栏高度
     *
     * @return
     */
    val statusBarHeight: Int
        get() {
            var result = 0
            val resourceId = getApplication().resources
                .getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = getApplication().resources
                    .getDimensionPixelSize(resourceId)
            }
            return result
        }

    val actionBarHeight: Int
        get() {
            var actionBarHeight = 0
            val tv = TypedValue()
            if (getApplication().theme
                    .resolveAttribute(R.attr.actionBarSize, tv, true)
            ) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(
                    tv.data, getApplication().resources.displayMetrics
                )
            }
            return actionBarHeight
        }

    /**
     * 关闭输入法
     *
     * @param act
     */
    fun closeInputMethod(act: Activity) {
        val view = act.window.peekDecorView()
        if (view != null) {
            val inputMethodManager =
                act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * 开启输入法
     *
     * @param act
     */
    fun openInputMethod(act: Activity, view: View?) {
        val inputMethodManager =
            act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // 接受软键盘输入的编辑文本或其它视图
        inputMethodManager.showSoftInput(
            view,
            InputMethodManager.SHOW_FORCED
        )
    }

    /**
     * 判断应用是否处于后台状态
     *
     * @return
     */
    val isBackground: Boolean
        get() {
            val am = getApplication()
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val tasks = am.getRunningTasks(1)
            if (!tasks.isEmpty()) {
                val topActivity = tasks[0].topActivity
                if (topActivity!!.packageName != getApplication()
                        .packageName
                ) {
                    return true
                }
            }
            return false
        }

    /**
     * 复制文本到剪贴板
     *
     * @param text
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun copyToClipboard(text: String?) {
        val cbm = getApplication()
            .getSystemService(Activity.CLIPBOARD_SERVICE) as ClipboardManager
        cbm.setPrimaryClip(
            ClipData.newPlainText(
                getApplication().packageName, text
            )
        )
    }

    /**
     * 获取SharedPreferences
     *
     * @return SharedPreferences
     */
    val sharedPreference: SharedPreferences
        get() = getApplication().getSharedPreferences(
            getApplication().packageName,
            Activity.MODE_PRIVATE
        )

    /**
     * 获取SharedPreferences
     *
     * @return SharedPreferences
     */
    fun getSharedPreference(name: String?): SharedPreferences {
        return getApplication()
            .getSharedPreferences(name, Activity.MODE_PRIVATE)
    }

    /**
     * 获取SharedPreferences
     *
     * @return SharedPreferences
     */
    fun getSharedPreference(name: String?, mode: Int): SharedPreferences {
        return getApplication().getSharedPreferences(name, mode)
    }

    /**
     * 经纬度测距
     *
     * @param jingdu1
     * @param weidu1
     * @param jingdu2
     * @param weidu2
     * @return
     */
    fun distance(
        jingdu1: Double,
        weidu1: Double,
        jingdu2: Double,
        weidu2: Double
    ): Double {
        var weidu1 = weidu1
        var weidu2 = weidu2
        val a: Double
        val b: Double
        val R: Double
        R = 6378137.0 // 地球半径
        weidu1 = weidu1 * Math.PI / 180.0
        weidu2 = weidu2 * Math.PI / 180.0
        a = weidu1 - weidu2
        b = (jingdu1 - jingdu2) * Math.PI / 180.0
        val d: Double
        val sa2: Double
        val sb2: Double
        sa2 = Math.sin(a / 2.0)
        sb2 = Math.sin(b / 2.0)
        d = (2
                * R
                * Math.asin(
            Math.sqrt(
                sa2 * sa2 + (Math.cos(weidu1)
                        * Math.cos(weidu2) * sb2 * sb2)
            )
        ))
        return d
    }

    /**
     * 取APP版本名
     *
     * @return
     */
    val appVersionName: String
        get() = try {
            val mPackageManager =
                getApplication().packageManager
            val _info = mPackageManager.getPackageInfo(
                getApplication().packageName, 0
            )
            _info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }

    fun MD5(data: ByteArray?): String {
        var md5: MessageDigest? = null
        try {
            md5 = MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        md5!!.update(data)
        val m = md5.digest() //加密
        return Base64.encodeToString(m, Base64.DEFAULT)
    }

    fun saveVideo(videoFilePath: String, context: Context) {
        if (!TextUtils.isEmpty(videoFilePath) && File(videoFilePath).isFile) {
            val file = File(videoFilePath)
            val name =
                videoFilePath.substring(videoFilePath.lastIndexOf('/'), videoFilePath.length)
            val localContentValues = ContentValues()
            localContentValues.put(MediaStore.Video.Media.DATA, file.path)
            localContentValues.put(MediaStore.Video.Media.TITLE, file.name)
            localContentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            localContentValues.put(MediaStore.Video.Media.SIZE, file.length())
            localContentValues.put(MediaStore.Video.Media.DISPLAY_NAME, file.name)
            val localContentResolver = context.contentResolver
            val localUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            localContentResolver.insert(localUri, localContentValues)
        } else {
//            JUtils.Toast(MyApplication.Companion.getApplication().getString(R.string.File_does_not_exist));
        }
    }


    fun isEmail(email: String?): Boolean {
        val str =
            "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"
        val p = Pattern.compile(str)
        val m = p.matcher(email)
        return m.matches()
    }

    /**
     * dip-->px
     */
    fun dip2Px(dip: Int): Int {
        // px/dip = density;
        val density =
            getApplication().resources.displayMetrics.density
        return (dip * density + .5f).toInt()
    }

    /**
     * px-->dip
     */
    fun px2Dip(px: Int): Int {
        // px/dip = density;
        val density =
            getApplication().resources.displayMetrics.density
        return (px / density + .5f).toInt()
    }


    fun isIP(adder: String): Boolean {
        if (adder.length < 7 || adder.length > 15 || "" == adder) {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        val rexp =
            "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}"

        val pat: Pattern = Pattern.compile(rexp)

        val mat: Matcher = pat.matcher(adder)

        val ipAddress: Boolean = mat.find()

        //============对之前的ip判断的bug在进行判断
        if (ipAddress) {
            val split = adder.split("\\.")

            return split.size == 4
        }

        return ipAddress
    }


}