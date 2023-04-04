package com.ihdyo.githubuser.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ihdyo.githubuser.ui.adapter.UserAdapter
import com.ihdyo.githubuser.ui.adapter.PagerAdapter.Companion.ARGS_USERNAME
import com.ihdyo.githubuser.data.Result
import com.ihdyo.githubuser.data.remote.response.SimpleUser
import com.ihdyo.githubuser.databinding.FragmentFollowingBinding
import com.ihdyo.githubuser.ui.viewmodel.FollowingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class FollowingFragment : Fragment() {
    private lateinit var binding: FragmentFollowingBinding
    private val followingViewModel: FollowingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString(ARGS_USERNAME) ?: ""
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            launch {
                followingViewModel.following.collect {
                    when (it) {
                        is Result.Loading -> binding.pbLoading.visibility = View.VISIBLE
                        is Result.Error -> binding.pbLoading.visibility = View.GONE
                        is Result.Success -> {
                            if (it.data.size > 0) {
                                val linearLayoutManager = LinearLayoutManager(activity)
                                val listAdapter = UserAdapter(it.data)

                                binding.rvUsers.apply {
                                    layoutManager = linearLayoutManager
                                    adapter = listAdapter
                                    setHasFixedSize(true)
                                }

                                listAdapter.setOnItemClickCallback(object :
                                    UserAdapter.OnItemClickCallback {
                                    override fun onItemClicked(user: SimpleUser) {
                                        Intent(activity, DetailUserActivity::class.java).apply {
                                            putExtra(DetailUserActivity.EXTRA_DETAIL, user.login)
                                        }.also {
                                            startActivity(it)
                                        }
                                    }

                                })
                            } else binding.tvStatus.visibility = View.VISIBLE
                            binding.pbLoading.visibility = View.GONE
                        }
                    }
                }
            }
            launch {
                followingViewModel.isLoaded.collect { loaded ->
                    if (!loaded) followingViewModel.getFollowing(username)
                }
            }
        }
    }
}