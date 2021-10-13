package com.junerver.cloudnote.ui.activity

import com.junerver.cloudnote.R
import com.junerver.cloudnote.utils.NetUtils
import android.text.TextUtils
import android.content.Intent
import com.dslx.digtalclassboard.net.BmobMethods
import com.idealworkshops.idealschool.utils.SpUtils
import com.junerver.cloudnote.databinding.ActivityLoginBinding
import com.junerver.cloudnote.databinding.LoginRegisterBarBinding
import kotlinx.coroutines.launch

class LoginActivity: BaseActivity<ActivityLoginBinding>(){

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
                   val resp = BmobMethods.INSTANCE.login(mLoginUsername, mLoginPassword)
                   closeProgress()
                   if (resp.contains("error")) {
                       showShortToast(getString(R.string.user_pass_err))
                   } else {
                       //存储用户信息
                       SpUtils.encode("USER_INFO", resp)
                       startActivity(Intent(mContext, MainActivity::class.java))
                   }


               }
            }
        }
        viewBinding.tvRegister.setOnClickListener {
            startActivity(Intent(mContext, RegisterActivity::class.java))
        }
        loginRegisterBarBinding.ivBack.setOnClickListener { finish() }
    }

}