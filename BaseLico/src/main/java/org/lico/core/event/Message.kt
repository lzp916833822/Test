package org.lico.core.event

/**
 * @author: lico
 * @create：2020/5/25
 * @describe：
 */
class Message @JvmOverloads constructor(
    var code: Int = 0,
    var msg: String = "",
    var arg1: Int = 0,
    var arg2: Int = 0,
    var obj: Any? = null
)