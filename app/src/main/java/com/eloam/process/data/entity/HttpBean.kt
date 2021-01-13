package com.eloam.process.data.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.io.Serializable

/**
 * @author: lzp
 * @create：2020/6/3
 * @describe：
 */
@Entity
data class MyLogInfo(
    @Id var id: Long = 0,
    var mac: String,
    var sn: String,
    var reportTime: Long,
    var employeeNo: String,
    var itemName: String,
    var terminalInfo: String,
    var filePath: String
)

data class PostData(
    var data: String
)

data class AdPost(
    var sbbh: String,
    var lsh: Int,
    var bfjg: Int
)

@Entity
data class AdInfo(
    @Id var id: Long = 0,
    var sbbh: String,
    var bfjg: String,
    var default_pic: String,
    var pic1: String,
    var pic2: String,
    var pic3: String,
    var pic4: String,
    var pic5: String,
    var pic6: String,
    var pic7: String,
    var pic8: String,
    var pic9: String,
    var code: Int,
    var mode: Int,
    var default_video: String,
    var video1: String,
    var video2: String,
    var video3: String,
    var video4: String,
    var video5: String
) : Serializable

data class InfoResult(
    var message: String,
    var success: Boolean
)