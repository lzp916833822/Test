package com.eloam.process.data.entity

data class StatueAnim(
    var type: Int,
    var state: Int//0未开启，1进行中，2已完成
)


data class StatueResult(
    var type: Int,
    var state: Int,//0未开启，1开启成功，2开启失败
    var data: String//识别的数据，QR Code or IC Card
)

data class StatueOpenDevice(
    var type: Int,
    var state: Int//0未开启，1打开，2关闭
)

data class StatueOpen(
    var type: Int,
    var state: Int//-2不做处理，-1初始化失败,0初始化成功开启，1开启，2关闭
)

data class UploadStatueResult(
    var filePath: String,//文件路径
    var id: Long,//数据库id
    var index: Int,//数组坐标
    var result: Int//结果0成功，其它失败
)

data class PostRequestBody(
    var pageNum: Int,//当前页
    var pageSize: Int,//当前数量
    var sn: String,
    var mac: String
)