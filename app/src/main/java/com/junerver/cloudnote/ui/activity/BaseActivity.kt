package com.junerver.cloudnote.ui.activity

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Build
import android.view.LayoutInflater
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.reflect.ParameterizedType
import kotlin.coroutines.CoroutineContext

/**
 * Created by Junerver on 2016/8/31.
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() , CoroutineScope by MainScope() {

    protected lateinit var mContext: Context
    private var mProgressDialog: ProgressDialog? = null
    protected lateinit var viewBinding: VB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        initContentView()
        initData()
        initView()
        setListeners()
        mProgressDialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ProgressDialog(this, R.style.Theme_Material_Light_Dialog_Alert)
        } else {
            ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT)
        }
        mProgressDialog!!.setMessage("请稍等...")
        //注册事件总线
//            EventBus.getDefault().register(this);
    }

    /**
     * 如果使用绑定布局(layout包裹), 不能使用setContentView
     * 否则会修改根布局view的默认tag, 不需要修改tag的请无视
     * 使用ViewBinding自动绑定布局, 替代ButterKnife
     */
    protected fun initContentView() {
        initViewBinding("inflate", LayoutInflater::class.java, layoutInflater)
        if (viewBinding != null) {
            setContentView(viewBinding!!.root)
        }
    }

    /**
     * 反射初始化ViewBinding
     * ActivityBaseBinding.inflate()
     * ActivityBaseBinding.bind()
     * 具体getActualTypeArguments取[0]还是[1]
     * 取决于BaseActivity的泛型的第几位, 此方法为VB是[0]
     */
    protected fun initViewBinding(name: String?, cls: Class<*>?, obj: Any?) {
        val superclass = javaClass.genericSuperclass
        if (superclass != null) {
            val aClass = (superclass as ParameterizedType).actualTypeArguments[0] as Class<*>
            try {
                val method = aClass.getDeclaredMethod(name, cls)
                method.isAccessible = true
                viewBinding = method.invoke(null, obj) as VB
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        //注销事件总线
//        EventBus.getDefault().unregister(this);
    }

    override fun onDestroy() {
        super.onDestroy()
        // 当 Activity 销毁的时候取消该 Scope 管理的 job。
        // 这样在该 Scope 内创建的子 Coroutine 都会被自动的取消。
        cancel()
    }

    /**
     * @param msg toast的提示信息
     */
    protected fun showShortToast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


    /**
     * @param msg toast的提示信息
     */
    protected fun showLongToast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    /**
     * 打开进度条dialog
     */
    fun showProgress() {
        if (mProgressDialog != null && !mProgressDialog!!.isShowing) {
            mProgressDialog!!.show()
        }
    }

    /**
     * 关闭进度条dialog
     */
    fun closeProgress() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    /**
     * 视图初始化
     */
    protected abstract fun initView()

    /**
     * 数据初始化
     */
    protected abstract fun initData()

    /**
     * 设置监听器
     */
    protected abstract fun setListeners()
}