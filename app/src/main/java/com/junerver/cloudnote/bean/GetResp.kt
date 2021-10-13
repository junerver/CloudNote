package com.junerver.cloudnote.bean

/**
 * Created by houwenjun on 2019-05-24 BmoB 云数据库查询结果
 */
data class GetResp(var results: List<Results>) {
    data class Results(var address: String,
                       var createdAt: String,
                       var deviceDes: String,
                       var deviceId: String,
                       var lngLat: String,
                       var location: String,
                       var nurseryId: String,
                       var nurseryName: String,
                       var objectId: String,
                       var updatedAt: String,
                       var verCode: String,
                       var verName: String)
}