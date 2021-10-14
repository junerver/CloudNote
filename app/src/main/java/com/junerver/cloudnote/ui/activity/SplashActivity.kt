package com.junerver.cloudnote.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.junerver.cloudnote.R
import android.content.Intent
import android.os.Handler
import com.idealworkshops.idealschool.utils.SpUtils
import com.junerver.cloudnote.Constants
import com.junerver.cloudnote.ui.activity.MainActivity
import com.junerver.cloudnote.ui.activity.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //一个延时跳转效果
        Handler().postDelayed({
            isFirstRun
            finish()
        }, 1000)
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