package com.junerver.cloudnote

import android.app.Application
import android.content.Context
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.tencent.mmkv.MMKV
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy

import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy

import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator

import com.elvishew.xlog.printer.file.FilePrinter

import com.elvishew.xlog.printer.ConsolePrinter

import com.elvishew.xlog.printer.AndroidPrinter

import com.elvishew.xlog.interceptor.BlacklistTagsFilterInterceptor

import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.printer.Printer


class CloudNoteApp : Application() {

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        XLog.init(LogLevel.ALL)
        initXlog()

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




        XLog.init( // 初始化 XLog
            config,  // 指定日志配置，如果不指定，会默认使用 new LogConfiguration.Builder().build()
        )
    }
}