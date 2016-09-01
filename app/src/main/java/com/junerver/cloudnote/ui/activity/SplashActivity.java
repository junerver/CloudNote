package com.junerver.cloudnote.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.junerver.cloudnote.R;
import com.junerver.cloudnote.utils.SPUtils;
import com.zhy.http.okhttp.utils.L;

public class SplashActivity extends BaseActivity {


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
        return R.layout.activity_splash;
    }

    public void isFirstRun(Context context) {
        boolean isFirstRun= (boolean) SPUtils.get(context, "isFirstRun", true);
        if (isFirstRun) {
            //第一次运行打开登录界面
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }else {
            //不是第一次运行，找到SP文件中的用户名密码
            String loginedUsername = (String) SPUtils.get(context, "LoginedUsername", "");
            String loginedPassword = (String) SPUtils.get(context, "LoginedPassword", "");
            //使用最后一次保存的用户名密码访问服务器验证，

            // TODO: 2016/8/31  通过服务器验证账密是否正确并返回值
            boolean isAuthPass =true;
            if (isAuthPass) {
                //通过跳转主页面
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                //如果不通过则停留在登录页面，并且toast提示！
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                showShortToast("用户名或密码错误！！！");
            }
        }
    }
}
