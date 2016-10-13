package com.junerver.cloudnote.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.junerver.cloudnote.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigFragment extends BaseFragment {


    @BindView(R.id.ivUserAvatar)
    ImageView mIvUserAvatar;
    @BindView(R.id.btnInOut)
    Button mBtnInOut;

    @Override
    protected void init() {
        BmobUser bmobUser = BmobUser.getCurrentUser();
        if (bmobUser != null) {
            mBtnInOut.setText(R.string.login_out);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_config;
    }


    @OnClick(R.id.btnInOut)
    public void onClick() {
    }
}
