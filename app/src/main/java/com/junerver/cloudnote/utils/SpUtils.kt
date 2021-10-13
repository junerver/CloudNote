package com.idealworkshops.idealschool.utils

import android.os.Parcelable
import com.tencent.mmkv.MMKV
import java.util.*
import kotlin.reflect.KClass

/**
 * @Author Junerver
 * @Date 2020/12/17-15:48
 * @Email junerver@gmail.com
 * @Version v1.0
 * @Description  使用腾讯MMKV来代替SP存储，封装工具类，
 *          SpUtil.INSTANCE.encode()
            SpUtil.INSTANCE.decodeStringSet()

 */
object SpUtils {

    private var mmkv: MMKV = MMKV.defaultMMKV()

    fun encode(key: String, value: Any?) {
        when (value) {
            is String -> mmkv.encode(key, value)
            is Float -> mmkv.encode(key, value)
            is Boolean -> mmkv.encode(key, value)
            is Int -> mmkv.encode(key, value)
            is Long -> mmkv.encode(key, value)
            is Double -> mmkv.encode(key, value)
            is ByteArray -> mmkv.encode(key, value)
            is Nothing -> return
        }
    }

    fun <T : Parcelable> encode(key: String, t: T?) {
        if(t ==null){
            return
        }
        mmkv.encode(key, t)
    }
    

    fun decodeInt(key: String): Int {
        return mmkv.decodeInt(key, 0)
    }

    fun decodeDouble(key: String): Double {
        return mmkv.decodeDouble(key, 0.00)
    }

    fun decodeLong(key: String): Long {
        return mmkv.decodeLong(key, 0L)
    }

    fun decodeBoolean(key: String): Boolean {
        return mmkv.decodeBool(key, false)
    }

    fun decodeFloat(key: String): Float {
        return mmkv.decodeFloat(key, 0F)
    }

    fun decodeByteArray(key: String): ByteArray {
        return mmkv.decodeBytes(key)!!
    }

    fun decodeString(key: String): String {
        return mmkv.decodeString(key, "")!!
    }

    fun <T : Parcelable> decodeParcelable(key: String, tClass: Class<T>): T {
        return mmkv.decodeParcelable(key, tClass)!!
    }



    fun <T : Any> decode(key: String, clazz: KClass<out T>):T{
            return when (clazz) {
                Int::class -> decodeInt(key)
                Double::class-> decodeDouble(key)
                String::class -> decodeString(key)
                Long::class -> decodeLong(key)
                Float::class -> decodeFloat(key)
                Boolean::class -> decodeBoolean(key)
                ByteArray::class -> decodeByteArray(key)
                else -> throw IllegalArgumentException("MMKV 类型错误")
            } as T
    }


    fun removeKey(key: String) {
        mmkv.removeValueForKey(key)
    }

    fun clearAll() {
        mmkv.clearAll()
    }
}

