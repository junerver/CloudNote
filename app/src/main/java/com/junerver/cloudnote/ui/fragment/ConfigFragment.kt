package com.junerver.cloudnote.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.edusoa.ideallecturer.toBean
import com.idealworkshops.idealschool.utils.SpUtils
import com.junerver.cloudnote.Constants
import com.junerver.cloudnote.R
import com.junerver.cloudnote.bean.UserInfoResp
import com.junerver.cloudnote.databinding.FragmentConfigBinding
import com.junerver.cloudnote.ui.activity.LoginActivity

/**
 * A simple [Fragment] subclass.
 */
class ConfigFragment : BaseFragment() {

    private var _binding: FragmentConfigBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentConfigBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun init() {
        //获取用户信息
        val userInfoStr = SpUtils.decodeString(Constants.SP_USER_INFO)

        val userInfo = userInfoStr.toBean<UserInfoResp>()
        if (userInfo.username.isNotEmpty()) {
            binding.tvUserName.text = userInfo.username
        }

        binding.btnLogout.setOnClickListener {
            SpUtils.clearAll()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }
    }

}