package org.lico.core.base

/**
 * @author: lico
 * @create：2020/5/25
 * @describe：
 */
data class BaseResult<T>(
    val errorMsg: String,
    val errorCode: Int,
    val data: T) : IBaseResponse<T>{

    override fun code() = errorCode

    override fun msg() = errorMsg

    override fun data() = data

    override fun isSuccess() = errorCode == 0
}
