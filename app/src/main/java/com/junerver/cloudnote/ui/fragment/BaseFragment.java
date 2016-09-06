package com.junerver.cloudnote.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.junerver.cloudnote.R;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutId = getLayoutId();
        if (layoutId == 0) {
            throw new IllegalStateException("Please specify root layout resource id for " + getClass().getSimpleName());
        } else {
            View parentView = inflater.inflate(layoutId, null);
            ButterKnife.bind(this, parentView);
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

}
