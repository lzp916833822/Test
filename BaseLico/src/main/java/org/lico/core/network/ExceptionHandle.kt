package org.lico.core.network

import android.net.ParseException
import android.util.Log
import android.util.MalformedJsonException
import com.google.gson.JsonParseException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException

/**
 * @author: lico
 * @create：2020/5/25
 * @describe：
 */
object ExceptionHandle {

    fun handleException(e: Throwable): ResponseThrowable {
        val ex: ResponseThrowable
        if (e is HttpException) {
            ex = ResponseThrowable(ERROR.HTTP_ERROR, e)
        } else if (e is JsonParseException
            || e is JSONException
            || e is ParseException || e is MalformedJsonException
        ) {
            ex = ResponseThrowable(ERROR.PARSE_ERROR, e)
        } else if (e is ConnectException) {
            ex = ResponseThrowable(ERROR.NETWORD_ERROR, e)
        } else if (e is javax.net.ssl.SSLException) {
            ex = ResponseThrowable(ERROR.SSL_ERROR, e)
        } else if (e is java.net.SocketTimeoutException) {
            ex = ResponseThrowable(ERROR.TIMEOUT_ERROR, e)
        } else if (e is java.net.UnknownHostException) {
            ex = ResponseThrowable(ERROR.TIMEOUT_ERROR, e)
        } else {
          //  Log.e("zzkong", "哈哈哈: " +e.message);
            ex = if (e.message.toString().isNullOrEmpty()){
             //   Log.e("zzkong", "哦哦哦: " + e.message!!)
                ResponseThrowable(1000, e.message!!, e)
            }
            else ResponseThrowable(ERROR.UNKNOWN, e)
        }
        return ex
    }
}