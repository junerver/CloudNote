package com.junerver.cloudnote.ui.activity

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.junerver.cloudnote.R
import com.junerver.cloudnote.databinding.ActivityMainBinding
import com.junerver.cloudnote.ui.fragment.ConfigFragment
import com.junerver.cloudnote.ui.fragment.NoteFragment
import com.junerver.cloudnote.ui.widget.TabView
import java.util.HashMap

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val mTitle = arrayOf("笔记", "设置")
    private val mIconSelect = intArrayOf(R.drawable.docker_tab_doc_selected, R.drawable.docker_tab_setting_selected)
    private val mIconNormal = intArrayOf(R.drawable.docker_tab_doc_normal, R.drawable.docker_tab_setting_normal)
    private lateinit var mViewPager: ViewPager
    private lateinit var mTabView: TabView
    private lateinit var mFragmentMap: MutableMap<Int, Fragment>

    override fun initView() {
        mFragmentMap = HashMap<Int, Fragment>()
        mViewPager = viewBinding.idViewPager
        mViewPager.offscreenPageLimit = 2
        mViewPager.adapter = PageAdapter(supportFragmentManager)
        mTabView = viewBinding.idTab
        mTabView.setViewPager(mViewPager)
    }

    override fun initData() {}
    override fun setListeners() {}
    private fun getFragment(position: Int): Fragment {
        var fragment = mFragmentMap[position]
        if (fragment == null) {
            when (position) {
                0 -> fragment = NoteFragment()
                1 -> fragment = ConfigFragment()
            }
            mFragmentMap[position] = fragment!!
        }
        return fragment!!
    }

    protected fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    internal inner class PageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm),
        TabView.OnItemIconTextSelectListener {
        override fun getItem(position: Int): Fragment {
            return getFragment(position)
        }

        override fun onIconSelect(position: Int): IntArray {
            val icon = IntArray(2)
            icon[0] = mIconSelect[position]
            icon[1] = mIconNormal[position]
            return icon
        }

        override fun onTextSelect(position: Int): String {
            return mTitle[position]
        }

        override fun getCount(): Int {
            return mTitle.size
        }
    }
}