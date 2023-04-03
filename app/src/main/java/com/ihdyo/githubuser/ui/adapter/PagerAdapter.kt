package com.ihdyo.githubuser.ui.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ihdyo.githubuser.ui.view.FollowersFragment
import com.ihdyo.githubuser.ui.view.FollowingFragment

class PagerAdapter(activity: AppCompatActivity, private val username: String) :
    FragmentStateAdapter(activity) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> FollowersFragment()
            else -> FollowingFragment()
        }.apply {
            arguments = Bundle().apply { putString(ARGS_USERNAME, username) }
        }

    companion object {
        const val ARGS_USERNAME = "username"
    }
}