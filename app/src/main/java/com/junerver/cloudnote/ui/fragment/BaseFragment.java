package com.junerver.cloudnote.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.junerver.cloudnote.R;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {
    protected Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutId = getLayoutId();
        if (layoutId == 0) {
            throw new IllegalStateException("Please specify root layout resource id for " + getClass().getSimpleName());
        } else {
            View parentView = inflater.inflate(layoutId, null);
            ButterKnife.bind(this, parentView);
            mContext = getActivity().getApplicationContext();
            init();
            return parentView;
        }
    }

    /**
     * 初始化
     */
    protected abstract void init();

    /**
     * @return  布局ID
     */
    protected abstract int getLayoutId();

    protected void showShortToast(String msg) {
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }
}
