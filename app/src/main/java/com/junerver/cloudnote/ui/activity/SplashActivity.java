package com.junerver.cloudnote.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.junerver.cloudnote.Constants;
import com.junerver.cloudnote.R;
import com.junerver.cloudnote.observable.LoginRegisterObservable;
import com.junerver.cloudnote.utils.SPUtils;
import com.zhy.http.okhttp.utils.L;

import rx.Observer;

public class SplashActivity extends BaseActivity implements Observer<String> {


    @Override
    protected void initView() {
        //一个延时跳转效果
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isFirstRun(mContext);
                SplashActivity.this.finish();
            }
        }, 1000);
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
            String loginedUsername = (String) SPUtils.get(context, "loginedUsername", "");
            String loginedPassword = (String) SPUtils.get(context, "loginedPassword", "");
            //使用最后一次保存的用户名密码访问服务器验证，

            // TODO: 2016/8/31  通过服务器验证账密是否正确并返回值
            LoginRegisterObservable.getObservable(Constants.GET_LOGIN,loginedUsername,loginedPassword)
                    .subscribe(this);

        }
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(String s) {

        if (s=="1") {
            //通过跳转主页面
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            //如果不通过则停留在登录页面，并且toast提示！
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            showShortToast("用户名或密码错误！！！");
        }
    }
}
