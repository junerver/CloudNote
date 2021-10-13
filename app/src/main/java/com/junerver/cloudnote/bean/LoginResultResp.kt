package com.junerver.cloudnote.bean

/**
 * @Description 登录响应
 * @Author Junerver
 * Created at 2021/10/12 16:44
 */
data class LoginResultResp(
    var createdAt: String = "",
    var objectId: String = "",
    var sessionToken: String = "",
    var updatedAt: String = "",
    var username: String = ""
)