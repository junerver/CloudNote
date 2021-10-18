package com.junerver.cloudnote.ui.activity

import android.content.Intent
import android.text.TextUtils
import com.junerver.cloudnote.net.BmobMethods
import com.edusoa.ideallecturer.fetchNetwork
import com.edusoa.ideallecturer.toBean
import com.edusoa.ideallecturer.toJson
import com.elvishew.xlog.XLog
import com.idealworkshops.idealschool.utils.SpUtils
import com.junerver.cloudnote.Constants
import com.junerver.cloudnote.R
import com.junerver.cloudnote.bean.ErrorResp
import com.junerver.cloudnote.bean.UserInfoResp
import com.junerver.cloudnote.databinding.ActivityLoginBinding
import com.junerver.cloudnote.databinding.LoginRegisterBarBinding
import com.junerver.cloudnote.utils.NetUtils
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private lateinit var mLoginUsername: String
    private lateinit var mLoginPassword: String
    private lateinit var loginRegisterBarBinding: LoginRegisterBarBinding

    override fun initView() {
        loginRegisterBarBinding = LoginRegisterBarBinding.bind(viewBinding.llRoot)
    }

    override fun initData() {}
    override fun setListeners() {
        viewBinding.btnLogin.setOnClickListener {
            mLoginUsername = viewBinding.etLoginUsername.text.toString().trim()
            mLoginPassword = viewBinding.etLoginPassword.text.toString().trim()

            //都不是空
            if (!NetUtils.isConnected(mContext)) {
                showShortToast(getString(R.string.no_connect))
            } else if (TextUtils.isEmpty(mLoginUsername)) {
                viewBinding.etLoginUsername.requestFocus()
                viewBinding.etLoginUsername.error = getString(R.string.username_nonblank)
                return@setOnClickListener
            } else if (TextUtils.isEmpty(mLoginPassword)) {
                viewBinding.etLoginPassword.requestFocus()
                viewBinding.etLoginPassword.error = getString(R.string.password_nonblank)
                return@setOnClickListener
            } else {
                showProgress()
                launch {
                    fetchNetwork({
                        BmobMethods.INSTANCE.login(mLoginUsername, mLoginPassword)
                    }, { result ->
                        XLog.d(result)
                        closeProgress()
                        val userInfo = result.toBean<UserInfoResp>()
                        SpUtils.encode(Constants.SP_USER_INFO, userInfo.toJson())
                        SpUtils.encode(Constants.SP_USER_ID,userInfo.objectId)
                        startActivity(Intent(mContext, MainActivity::class.java))
                        finish()
                    }, {errorBody, errorMsg, code ->
                        closeProgress()
                        errorBody?.let {
                            val bean = it.toBean<ErrorResp>()
                            showLongToast(errorMsg+bean.error)
                        }
                    })
                }
            }
        }
        viewBinding.tvRegister.setOnClickListener {
            startActivity(Intent(mContext, RegisterActivity::class.java))
        }
        loginRegisterBarBinding.ivBack.setOnClickListener { finish() }
    }

}