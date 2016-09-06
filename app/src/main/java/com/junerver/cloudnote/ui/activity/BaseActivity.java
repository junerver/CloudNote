package com.junerver.cloudnote.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

/**
 * Created by Junerver on 2016/8/31.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    private static ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        int layout = getLayoutId();
        if (layout == 0) {
            throw new IllegalStateException("Please specify root layout resource id for " + getClass().getSimpleName());
        } else {
            setContentView(layout);
            ButterKnife.bind(this);
            initData();
            initView();
            setListeners();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mProgressDialog = new ProgressDialog(this, android.R.style.Theme_Material_Light_Dialog_Alert);
            } else {
                mProgressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
            }
            mProgressDialog.setMessage("请稍等...");
            //注册事件总线
//            EventBus.getDefault().register(this);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        //注销事件总线
//        EventBus.getDefault().unregister(this);
    }

    /**
     * @param msg toast的提示信息
     */
    protected void showShortToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 打开进度条dialog
     */
    public void showProgress() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    /**
     * 关闭进度条dialog
     */
    public void closeProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 视图初始化
     */
    protected abstract void initView();

    /**
     * 数据初始化
     */
    protected abstract void initData();

    /**
     * 设置监听器
     */
    protected abstract void setListeners();

    /**
     * @return  布局ID
     */
    protected abstract int getLayoutId();
}
