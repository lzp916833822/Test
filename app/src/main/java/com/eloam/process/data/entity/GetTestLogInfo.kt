package com.eloam.process.data.entity

data class GetTestLogInfo(
    val code: Int,
    val `data`: Data,
    val msg: String,
    val success: Boolean
)

data class Data(
    val firstPage: Boolean,
    val lastPage: Boolean,
    val list: List<DataInfo>,
    val orderBy: String,
    val pageNumber: Int,
    val pageSize: Int,
    val paras: Paras,
    val totalPage: Int,
    val totalRow: Int
)

data class DataInfo(
    val baseUrl: String,
    val createTime: Any,
    val employeeNo: String,
    val id: Int,
    val ip: String,
    val ipFrom: String,
    val itemId: Int,
    val itemName: String,
    val mac: String,
    val orgName: String,
    val reportTime: Long,
    val sn: String,
    val terminalInfo: String
)

data class Paras(
    val _orderBy: String,
    val _pageOffset: Int,
    val _pageSize: Int,
    val mac: String
)