package com.junerver.cloudnote.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.junerver.cloudnote.R;
import com.junerver.cloudnote.utils.NetUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.etRegisterUsername)
    EditText mEtRegisterUsername;
    @BindView(R.id.etRegisterPassword)
    EditText mEtRegisterPassword;
    @BindView(R.id.btnRegister)
    Button mBtnRegister;

    private String mRegisterUsername;
    private String mRegisterPassword;


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
        return R.layout.activity_register;
    }

    @OnClick(R.id.btnRegister)
    public void onClick() {
        mRegisterUsername =mEtRegisterUsername.getText().toString().trim();
        mRegisterPassword =mEtRegisterPassword.getText().toString().trim();
        //都不是空
        if (!NetUtils.isConnected(mContext)) {
            showShortToast("没有网络连接！！！");
        }else if (TextUtils.isEmpty(mRegisterUsername)) {
            mEtRegisterUsername.requestFocus();
            mEtRegisterUsername.setError("对不起，用户名不能为空");
            return;
        } else if (TextUtils.isEmpty(mRegisterPassword)) {
            mEtRegisterPassword.requestFocus();
            mEtRegisterPassword.setError("对不起，用密码不能为空");
            return;
        } else {
            showProgress();
            BmobUser bmobUser = new BmobUser();
            bmobUser.setUsername(mRegisterUsername);
            bmobUser.setPassword(mRegisterPassword);
            //注意：不能用save方法进行注册
            bmobUser.signUp(new SaveListener<BmobUser>() {
                @Override
                public void done(BmobUser s, BmobException e) {
                    closeProgress();
                    if(e==null){
                        showShortToast("注册成功");
                        startActivity(new Intent(mContext,MainActivity.class));
                    }else{
                        showShortToast("注册失败！换个用户名试试吧~");
                    }
                }
            });
        }

    }
}
