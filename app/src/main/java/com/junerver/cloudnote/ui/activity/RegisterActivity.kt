package com.junerver.cloudnote.ui.activity

import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.dslx.digtalclassboard.net.BmobMethods
import com.edusoa.ideallecturer.createJsonRequestBody
import com.edusoa.ideallecturer.toBean
import com.edusoa.ideallecturer.toJson
import com.idealworkshops.idealschool.utils.SpUtils
import com.junerver.cloudnote.R
import com.junerver.cloudnote.bean.ErrorResp
import com.junerver.cloudnote.bean.UserInfoResp
import com.junerver.cloudnote.databinding.ActivityRegisterBinding
import com.junerver.cloudnote.databinding.LoginRegisterBarBinding
import com.junerver.cloudnote.utils.NetUtils
import kotlinx.coroutines.launch

//注册用户
class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {

    private lateinit var mRegisterUsername: String
    private lateinit var mRegisterPassword: String
    private lateinit var loginRegisterBarBinding: LoginRegisterBarBinding

    override fun initView() {
        loginRegisterBarBinding = LoginRegisterBarBinding.bind(viewBinding.llRoot)
    }

    override fun initData() {}
    override fun setListeners() {
        viewBinding.btnRegister.setOnClickListener {
            mRegisterUsername = viewBinding.etRegisterUsername.text.toString().trim()
            mRegisterPassword = viewBinding.etRegisterPassword.text.toString().trim()
            //都不是空
            if (!NetUtils.isConnected(mContext)) {
                showShortToast(getString(R.string.no_connect))
            } else if (TextUtils.isEmpty(mRegisterUsername)) {
                viewBinding.etRegisterUsername.requestFocus()
                viewBinding.etRegisterUsername.error = getString(R.string.username_nonblank)
                return@setOnClickListener
            } else if (TextUtils.isEmpty(mRegisterPassword)) {
                viewBinding.etRegisterPassword.requestFocus()
                viewBinding.etRegisterPassword.error = getString(R.string.password_nonblank)
                return@setOnClickListener
            } else {
                showProgress()
                launch {
                    val map =
                        mapOf("username" to mRegisterUsername, "password" to mRegisterPassword)
                    val resp = BmobMethods.INSTANCE.register(map.toJson().createJsonRequestBody())
                    closeProgress()
                    if (resp.contains("error")) {
                        val bean = resp.toBean<ErrorResp>()
                        showShortToast(bean.error)
                    } else {
                        //保存用户信息
                        val userInfo = resp.toBean<UserInfoResp>()
                        userInfo.username = mRegisterUsername
                        SpUtils.encode("USER_INFO", userInfo.toJson())
                        showShortToast(getString(R.string.register_success))
                        startActivity(Intent(mContext, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}