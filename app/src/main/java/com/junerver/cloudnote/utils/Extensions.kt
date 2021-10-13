package com.edusoa.ideallecturer

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.util.regex.Pattern

fun Context.getDrawableRes(@DrawableRes id: Int): Drawable {
    return ContextCompat.getDrawable(this, id)!!
}

fun Context.getColorRes(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}

fun Context.inflater(resource: Int): View {
    return LayoutInflater.from(this).inflate(resource, null)
}


fun Context.inflater(resource: Int, root: ViewGroup, attachToRoot: Boolean): View {
    return LayoutInflater.from(this).inflate(resource, root, attachToRoot)
}

/**
 * 获取屏幕宽度
 *
 * @return 屏幕宽度
 */
fun Context.screenWidth(): Int {
    return resources.displayMetrics.widthPixels
}

/**
 * 获取屏幕高度
 *
 * @return 屏幕高度
 */
fun Context.screenHeight(): Int {
    return resources.displayMetrics.heightPixels
}

/**
 * 获取状态栏高度
 *
 * @return 状态栏高度（px）
 */
fun Context.statusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")

    var statusBarHeight = 0
    if (resourceId > 0) {
        statusBarHeight = resources.getDimensionPixelSize(resourceId)
    }

    return statusBarHeight
}

/**
 * 获取Version code
 *
 * @return version code
 */
fun Context.versionCode(): Int {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        packageManager.getPackageInfo(packageName, 0).versionCode
    } else {
        packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt()
    }
}

/**
 * 获取Version name
 *
 * @return version name
 */
fun Context.versionName(): String {
    return packageManager.getPackageInfo(packageName, 0).versionName
}

fun Context.packageName(): String {
    return packageName
}

/**
 * 获取像素密集度参数density
 *
 * @return density
 */
fun Context.density(): Float {
    return resources.displayMetrics.density
}


/**
 * 检查设备是否有虚拟键盘
 */
fun Context.checkDeviceHasNavigationBar(): Boolean {
    var hasNavigationBar = false
    val rs = this.resources
    val id = rs
        .getIdentifier("config_showNavigationBar", "bool", "android")
    if (id > 0) {
        hasNavigationBar = rs.getBoolean(id)
    }
    try {
        val systemPropertiesClass = Class.forName("android.os.SystemProperties")
        val m = systemPropertiesClass.getMethod("get", String::class.java)
        val navBarOverride = m.invoke(
            systemPropertiesClass,
            "qemu.hw.mainkeys"
        ) as String
        if ("1" == navBarOverride) {
            hasNavigationBar = false
        } else if ("0" == navBarOverride) {
            hasNavigationBar = true
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return hasNavigationBar
}

/**
 * @Description 适配安卓7.0中Uri，需要注意在AndroidManifest文件中注册——provider
 * @Author Junerver
 * Created at 2018/12/22 09:35
 * @param file 需要获取 uri 的 file
 * @return
 */
fun Context.getUriForFile(file: File): Uri {
    return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(
            this.applicationContext,
            "${this.packageName}.fileprovider",
            file
        )
    } else {
        Uri.fromFile(file)
    }
}

/**
 * @Description  获取清单文件中的metedata
 * @Author Junerver
 * Created at 2019/8/9 10:06
 * @param
 * @return
 */
inline fun <reified T> Context.getMeteData(key: String, def: T): T {
    val applicationInfo = this.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    val data = applicationInfo.metaData.get(key)
    return when (T::class) {
        Int::class -> data as Int
        String::class -> data as String
        Long::class -> (data as Float).toLong() //存储时以float类型保存，最大值不能超过
        Float::class -> data as Float
        Boolean::class -> data as Boolean
        else -> throw IllegalArgumentException("METE-DATA 类型错误")
    } as T
}

/**
 * @Description 隐藏Act的底部虚拟键盘
 * @Author Junerver
 * Created at 2019-06-26 11:04
 * @param
 * @return
 */
fun Activity.hideBottomUIMenu() {
    //隐藏虚拟按键，并且全屏
    if (Build.VERSION.SDK_INT in 12..18) { // lower api
        val v = this.window.decorView
        v.systemUiVisibility = View.GONE
    } else if (Build.VERSION.SDK_INT >= 19) {
        //for new api versions.
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions
    }
}


/*  扩展view */
fun View.gone() {
    this.visibility = View.GONE
}

fun kotlin.Any.gones(vararg views: View) {
    views.forEach {
        it.gone()
    }
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun kotlin.Any.invisibles(vararg views: View) {
    views.forEach {
        it.invisible()
    }
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun kotlin.Any.visibles(vararg views: View) {
    views.forEach {
        it.visible()
    }
}

/* EditText 扩展函数 */
fun EditText.textString(): String = this.text.toString().trim()

fun EditText.isEmpty(): Boolean = this.text.toString().trim().isEmpty()

fun EditText.isNotEmpty(): Boolean = this.text.toString().trim().isNotEmpty()

/* 清除输入框数据 */
fun EditText.clear() {
    setText("")
}

/**
 * EditText设置只能输入数字和小数点，小数点只能1个且小数点后最多只能2位
 */
fun EditText.setOnlyDecimal() {
    this.inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
    this.addTextChangedListener(object : TextWatcher {

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //这部分是处理如果输入框内小数点后有俩位，那么舍弃最后一位赋值，光标移动到最后
            if (s.toString().contains(".")) {
                if (s.length - 1 - s.toString().indexOf(".") > 2) {
                    setText(s.toString().subSequence(0, s.toString().indexOf(".") + 3))
                    setSelection(s.toString().subSequence(0, s.toString().indexOf(".") + 3).length)
                }
            }

            if (s.toString().trim().substring(0) == ".") {
                setText("0$s")
                setSelection(2)
            }

            if (s.toString().startsWith("0") && s.toString().trim().length > 1) {
                if (s.toString().substring(1, 2) != ".") {
                    setText(s.subSequence(0, 1))
                    setSelection(1)
                    return
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                       after: Int) {
        }

        override fun afterTextChanged(s: Editable) {

        }
    })
}

/**
 * 验证所有EditText是否为空
 * @return 只要有空，返回false
 */
fun checkAll(vararg all: EditText): Boolean {
    all.forEach {
        if (it.isEmpty()) {
            return false
        }
    }
    return true
}

/* TextView 扩展*/
fun TextView.drawableLeft(@DrawableRes id: Int) {
    val d = context.getDrawableRes(id)
    d.setBounds(0, 0, d.minimumWidth, d.minimumHeight)
    this.setCompoundDrawables(d, null, null, null)
}

fun TextView.drawableBottom(@DrawableRes id: Int) {
    val d = context.getDrawableRes(id)
    d.setBounds(0, 0, d.minimumWidth, d.minimumHeight)
    this.setCompoundDrawables(null, null, null, d)
}

fun TextView.drawableRight(@DrawableRes id: Int) {
    val d = context.getDrawableRes(id)
    d.setBounds(0, 0, d.minimumWidth, d.minimumHeight)
    this.setCompoundDrawables(null, null, d, null)
}

fun TextView.drawableTop(@DrawableRes id: Int) {
    val d = context.getDrawableRes(id)
    d.setBounds(0, 0, d.minimumWidth, d.minimumHeight)
    this.setCompoundDrawables(null, d, null, null)
}

/**
 * 修改TextView文字颜色
 */
fun TextView.color(@ColorRes id: Int) {
    val ctx = this.context
    this.setTextColor(ContextCompat.getColor(ctx, id))
}

/**
 * @Description tv数据trim
 * @Author Junerver
 * Created at 2018/12/22 09:31
 * @param
 * @return
 */
fun TextView.stringTrim(): String {
    return this.text.toString().trim()
}


/* String 扩展 */
/**
 * 验证是否手机
 */
fun String.isMobile(): Boolean {
    val regex = "(\\+\\d+)?1[34578]\\d{9}$"
    return Pattern.matches(regex, this)
}

/**
 * 验证是否电话
 */
fun String.isPhone(): Boolean {
    val regex = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$"
    return Pattern.matches(regex, this)

}

/**
 * 验证是否邮箱
 */
fun String.isEmail(): Boolean {
    val emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")
    val matcher = emailPattern.matcher(this)
    if (matcher.find()) {
        return true
    }
    return false
}

/**
 * 字符串安全转换到整型，转换失败返回0
 */
fun String.safeConvertToInt(): Int {
    return try {
        toInt()
    } catch (e: Exception) {
        0
    }
}

/**
 * 字符串安全转换到长整型，转换失败返回0
 */
fun String.safeConvertToLong(): Long {
    return try {
        toLong()
    } catch (e: Exception) {
        0L
    }
}

/**
 * 字符串安全转换到双精度类型，转换失败返回0
 */
fun String.safeConvertToDouble(): Double {
    return try {
        toDouble()
    } catch (e: Exception) {
        0.0
    }
}

/**
 * 字符串安全转换到短整型类型，转换失败返回0
 */
fun String.safeConvertToShort(): Short {
    return try {
        toShort()
    } catch (e: Exception) {
        0
    }
}

/** json相关 **/
fun Any.toJson(dateFormat: String = "yyyy-MM-dd HH:mm:ss", lenient: Boolean = false, excludeFields: List<String>? = null)
        = GsonBuilder().setDateFormat(dateFormat)
    .apply {
        if(lenient) setLenient()
        if(!excludeFields.isNullOrEmpty()){
            setExclusionStrategies(object : ExclusionStrategy {
                override fun shouldSkipField(f: FieldAttributes?): Boolean {
                    return f!=null && excludeFields.contains(f.name)
                }
                override fun shouldSkipClass(clazz: Class<*>?) = false
            })
        }
    }
    .create().toJson(this)!!

inline fun <reified T> String.toBean(dateFormat: String = "yyyy-MM-dd HH:mm:ss", lenient: Boolean = false)
        = GsonBuilder().setDateFormat(dateFormat)
    .apply {
        if(lenient) setLenient()
    }.create()
    .fromJson<T>(this, object : TypeToken<T>() {}.type)!!

val MEDIA_TYPE_JSON = MediaType.parse("Content-Type, application/json")

fun String.createJsonRequestBody(): RequestBody = RequestBody.create(MEDIA_TYPE_JSON,this)
