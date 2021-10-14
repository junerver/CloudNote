package com.junerver.cloudnote

/**
 * Created by Junerver on 2016/9/1.
 */
object Constants {
    //Bmob云后台APPID
    const val BMOB_APP_ID = "fefce5edf036867d6bbddca07d71599a"
    const val BMOB_REST_KEY = "2197ad856b9ea7ae9c96e93cf63dee6c"

    //数据库名称
    const val DB_NAME = "cloudnote"

    //用户的全部字段信息 对应UserInfoResp对象
    const val SP_USER_INFO = "USER_INFO"

    //用户id 用去其他请求快速读取
    const val SP_USER_ID = "USER_ID"

    //序列化时默认排除字段
    val DEFAULT_EXCLUDE_FIELDS: List<String> =
        listOf("objectId", "updatedAt", "createdAt", "_updatedTime", "_createdTime")
}