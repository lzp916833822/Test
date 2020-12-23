package org.lico.core.utils

import android.util.Log
import java.io.UnsupportedEncodingException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    /**
     * 获取今天是星期几
     * @return
     */
    val getWeek: String
        get() {
            val cal = Calendar.getInstance()
            val i = cal.get(Calendar.DAY_OF_WEEK)
            when (i) {
                1 -> return "星期日"
                2 -> return "星期一"
                3 -> return "星期二"
                4 -> return "星期三"
                5 -> return "星期四"
                6 -> return "星期五"
                7 -> return "星期六"
                else -> return ""
            }
        }

    /**
     * 判断2个时间大小
     * yyyy-MM-dd HH:mm 格式（自己可以修改成想要的时间格式）
     * @param startTime
     * @param endTime
     * @return
     */
    fun getTimeCompareSize(startTime: String, endTime: String): Boolean {
        if (endTime.trim { it <= ' ' } == "长期") {
            return true
        }
        val dateFormat = SimpleDateFormat("yyyy.MM.dd")//年-月-日
        try {
            val date1 = dateFormat.parse(startTime)//开始时间
            val date2 = dateFormat.parse(endTime)//结束时间
            val dateNow = dateFormat.parse(getTimeBySystem("yyyy.MM.dd")) //当前时间
            // 1 结束时间小于开始时间 2 开始时间与结束时间相同 3 结束时间大于开始时间
            return if (dateNow.time < date2.time && dateNow.time > date1.time && date2.time > date1.time) {
                true
            } else {
                false
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return false
    }

    fun getTimeBySystem(dateFormat: String): String {
        val simpleDateFormat = SimpleDateFormat(dateFormat)// HH:mm:ss
        //获取当前时间
        val date = Date(System.currentTimeMillis())
        return simpleDateFormat.format(date)
    }

    fun toUtf8(str: String): String? {
        var result: String? = null
        try {
            result = String(str.toByteArray(charset("UTF-8")))
        } catch (e: UnsupportedEncodingException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return result
    }

    fun getBirth(birth: String): String{
        return birth.replace("年","").replace("月","").replace("日","");
    }

}