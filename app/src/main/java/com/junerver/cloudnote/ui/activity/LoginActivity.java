package com.junerver.cloudnote.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.junerver.cloudnote.R;
import com.junerver.cloudnote.utils.NetUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity{

    @BindView(R.id.etLoginUsername)
    EditText mEtLoginUsername;
    @BindView(R.id.etLoginPassword)
    EditText mEtLoginPassword;
    @BindView(R.id.btnLogin)
    Button mBtnLogin;
    @BindView(R.id.tvRegister)
    TextView mTvRegister;
    @BindView(R.id.tvQuicklogin)
    TextView mTvQuicklogin;

    private String mLoginUsername;
    private String mLoginPassword;


    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @OnClick({R.id.btnLogin, R.id.tvRegister})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                mLoginUsername =mEtLoginUsername.getText().toString().trim();
                mLoginPassword =mEtLoginPassword.getText().toString().trim();

                //都不是空
                if (!NetUtils.isConnected(mContext)) {
                    showShortToast("没有网络连接！！！");
                }else if (TextUtils.isEmpty(mLoginUsername)) {
                    mEtLoginUsername.requestFocus();
                    mEtLoginUsername.setError("对不起，用户名不能为空");
                    return;
                } else if (TextUtils.isEmpty(mLoginPassword)) {
                    mEtLoginPassword.requestFocus();
                    mEtLoginPassword.setError("对不起，用密码不能为空");
                    return;
                } else {
                    showProgress();
                    BmobUser bmobUser = new BmobUser();
                    bmobUser.setUsername(mLoginUsername);
                    bmobUser.setPassword(mLoginPassword);
                    bmobUser.login( new SaveListener<BmobUser>() {
                        @Override
                        public void done(BmobUser bmobUser, BmobException e) {
                            closeProgress();
                            if(e==null){
                                //进入主页面
                                startActivity(new Intent(mContext,MainActivity.class));
                            }else{
                                showShortToast("用户名或者密码错误！！！");
                            }
                        }
                    });
                }
                break;
            case R.id.tvRegister:
                startActivity(new Intent(mContext, RegisterActivity.class));
                break;
        }
    }

}
