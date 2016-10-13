package com.junerver.cloudnote.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.junerver.cloudnote.CloudNoteApp;
import com.junerver.cloudnote.R;
import com.junerver.cloudnote.db.entity.Note;
import com.junerver.cloudnote.db.entity.NoteEntity;
import com.junerver.cloudnote.ui.fragment.ConfigFragment;
import com.junerver.cloudnote.ui.fragment.NoteFragment;
import com.junerver.cloudnote.ui.widget.TabView;

import java.security.acl.NotOwnerException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends BaseActivity {
    private String[] mTitle = {"笔记", "设置"};
    private int[] mIconSelect = {R.drawable.docker_tab_doc_selected, R.drawable.docker_tab_setting_selected};
    private int[] mIconNormal = {R.drawable.docker_tab_doc_normal, R.drawable.docker_tab_setting_normal};
    private ViewPager mViewPager ;
    private TabView mTabView ;
    private Map<Integer,Fragment> mFragmentMap ;
    public static String userObjId = BmobUser.getCurrentUser().getObjectId();


    @Override
    protected void initView() {

        mFragmentMap = new HashMap<>() ;
        mViewPager = (ViewPager)findViewById(R.id.id_view_pager) ;
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));
        mTabView = (TabView)findViewById(R.id.id_tab) ;
        mTabView.setViewPager(mViewPager);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListeners() {

    }

    private Fragment getFragment(int position){
        Fragment fragment = mFragmentMap.get(position) ;
        if(fragment == null){
            switch (position){
                case 0:
                    fragment = new NoteFragment() ;
                    break ;
                case 1:
                    fragment = new ConfigFragment();
                    break ;
            }
            mFragmentMap.put(position,fragment) ;
        }
        return fragment ;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    class PageAdapter extends FragmentPagerAdapter implements TabView.OnItemIconTextSelectListener{

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            return getFragment(position);
        }
        @Override
        public int[] onIconSelect(int position) {
            int icon[] = new int[2] ;
            icon[0] = mIconSelect[position] ;
            icon[1] = mIconNormal[position] ;
            return icon;
        }
        @Override
        public String onTextSelect(int position) {
            return mTitle[position];
        }

        @Override
        public int getCount() {
            return mTitle.length;
        }
    }

}
