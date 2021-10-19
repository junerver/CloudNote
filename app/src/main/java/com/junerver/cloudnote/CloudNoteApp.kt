package com.junerver.cloudnote

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.Printer
import com.tencent.mmkv.MMKV
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import org.litepal.LitePal

class CloudNoteApp : Application() {

    override fun onCreate() {
        super.onCreate()
        LitePal.initialize(this)
        MMKV.initialize(this)
        XLog.init(LogLevel.ALL)
        initXlog()
        UMConfigure.setLogEnabled(BuildConfig.DEBUG)
        UMConfigure.init(
            this,
            "616d093614e22b6a4f25b1b4",
            "default",
            UMConfigure.DEVICE_TYPE_PHONE,
            null
        )
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
    }

    private fun initXlog() {
        val config = LogConfiguration.Builder()
            .logLevel(
                if (BuildConfig.DEBUG) LogLevel.ALL // 指定日志级别，低于该级别的日志将不会被打印，默认为 LogLevel.ALL
                else LogLevel.NONE
            )
            .enableThreadInfo() // 允许打印线程信息，默认禁止
            .enableStackTrace(2) // 允许打印深度为 2 的调用栈信息，默认禁止
            .enableBorder() // 允许打印日志边框，默认禁止
            .build()
        val androidPrinter: Printer = AndroidPrinter(true)
        XLog.init(
            // 初始化 XLog
            config,  // 指定日志配置，如果不指定，会默认使用 new LogConfiguration.Builder().build()
            androidPrinter
        )
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        MultiDex.install(this)
    }
}