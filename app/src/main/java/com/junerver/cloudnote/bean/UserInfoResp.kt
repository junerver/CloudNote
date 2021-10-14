package com.junerver.cloudnote.bean

//用户数据 登录 or 注册
data class UserInfoResp(
    var createdAt: String = "",
    var objectId: String = "",
    var sessionToken: String = "",
    var updatedAt: String = "",
    var username: String = ""
)