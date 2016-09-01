package com.junerver.cloudnote.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.junerver.cloudnote.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tvUsername)
    TextView mTvUsername;

    @Override
    protected void initView() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        BmobUser bmobUser = BmobUser.getCurrentUser();
        if (bmobUser != null) {
            mTvUsername.setText(bmobUser.getUsername());
        }

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


}
