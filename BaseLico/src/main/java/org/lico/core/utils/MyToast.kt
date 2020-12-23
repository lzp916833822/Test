package org.lico.core.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import org.lico.core.R

/**
 * @author: lico
 * @Desc:
 */
object MyToast {
    var toast:Toast? = null
    var oldMsg:String = ""
    var oneTime = 0L
    var twoTime = 0L

    fun showToast(context: Context, message:String){
        val tempView = LayoutInflater.from(context).inflate(R.layout.toast_view, null)
        val textView = tempView.findViewById<TextView>(R.id.tips_toast)
        textView.setText(message)
        if (null == toast){
            toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
            toast!!.setGravity(Gravity.BOTTOM,0,100)
            toast!!.view = tempView
            toast!!.show()
            oneTime = System.currentTimeMillis()
        }else{
            twoTime = System.currentTimeMillis()
            if(message.equals(oldMsg)){
                if(twoTime - oneTime > Toast.LENGTH_SHORT){
                    toast!!.show()
                }
            }else{
                oldMsg = message
                textView.setText(oldMsg)
                toast!!.view = tempView
                toast!!.show()
            }
            oneTime = twoTime
        }
    }
}