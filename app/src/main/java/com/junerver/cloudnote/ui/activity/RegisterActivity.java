package com.junerver.cloudnote.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.junerver.cloudnote.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.etRegisterUsername)
    EditText mEtRegisterUsername;
    @BindView(R.id.etRegisterPassword)
    EditText mEtRegisterPassword;
    @BindView(R.id.btnRegister)
    Button mBtnRegister;

    private String mRegisterUsername;
    private String mRegisterPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnRegister)
    public void onClick() {
        mRegisterUsername =mEtRegisterUsername.getText().toString().trim();
        mRegisterPassword =mEtRegisterPassword.getText().toString().trim();
        //都不是空
        if (TextUtils.isEmpty(mRegisterUsername)) {
            mEtRegisterUsername.requestFocus();
            mEtRegisterUsername.setError("对不起，用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(mRegisterPassword)){
            mEtRegisterPassword.requestFocus();
            mEtRegisterPassword.setError("对不起，用密码不能为空");
            return;
        }
        // TODO: 2016/8/31  通过服务器验证 然后跳转页面
    }
}
