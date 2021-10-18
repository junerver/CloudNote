package com.junerver.cloudnote.ui.activity

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.junerver.cloudnote.R
import android.content.Intent
import android.os.Handler
import android.widget.Toast
import com.idealworkshops.idealschool.utils.SpUtils
import com.junerver.cloudnote.Constants
import com.junerver.cloudnote.ui.activity.MainActivity
import com.junerver.cloudnote.ui.activity.LoginActivity
import com.permissionx.guolindev.PermissionX

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        PermissionX.init(this)
            .permissions( Manifest.permission.READ_EXTERNAL_STORAGE)
            .explainReasonBeforeRequest()
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    //一个延时跳转效果
                    Handler().postDelayed({
                        isFirstRun
                        finish()
                    }, 1000)
                } else {
                    Toast.makeText(this, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
                }
            }


    }//缓存用户对象为空时， 可打开用户登录注册界面…// 允许用户使用应用

    //获取本地磁盘缓存的用户信息
    val isFirstRun: Unit
        get() {

            //获取本地磁盘缓存的用户信息
            val bmobUser = SpUtils.decodeString(Constants.SP_USER_INFO)

            if (bmobUser.isNotEmpty()) {
                // 允许用户使用应用
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                //缓存用户对象为空时， 可打开用户登录注册界面…
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
        }
}