package com.ihdyo.githubuser.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ihdyo.githubuser.R
import com.ihdyo.githubuser.ui.adapter.PagerAdapter
import com.ihdyo.githubuser.data.Result
import com.ihdyo.githubuser.data.local.UserEntity
import com.ihdyo.githubuser.data.remote.response.User
import com.ihdyo.githubuser.databinding.ActivityDetailBinding
import com.ihdyo.githubuser.ui.viewmodel.DetailViewModel
import com.ihdyo.githubuser.util.Helper.Companion.setAndVisible
import com.ihdyo.githubuser.util.Helper.Companion.setImageGlide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DetailUserActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!
    private var username: String? = null
    private var profileUrl: String? = null
    private var userDetail: UserEntity? = null
    private var isFavorite: Boolean? = false
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        username = intent.extras?.get(EXTRA_DETAIL) as String
        setContentView(binding.root)
        setViewPager()
        setToolbar(getString(R.string.profile))

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    detailViewModel.userDetail.collect { result ->
                        onDetailReceived(result)
                    }
                }
                launch {
                    detailViewModel.isFavorite(username ?: "").collect { state ->
                        isFavorite(state)
                        isFavorite = state
                    }
                }
                launch {
                    detailViewModel.isLoaded.collect { loaded ->
                        if (!loaded) detailViewModel.getDetail(username ?: "")
                    }
                }
            }
        }
        binding.btnOpen.setOnClickListener(this)
        binding.fabFavorite.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_open -> {
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(profileUrl)
                }.also {
                    startActivity(it)
                }
            }
            R.id.fab_favorite -> {
                if (isFavorite == true) {
                    userDetail?.let { detailViewModel.deleteFromFavorite(it) }
                    isFavorite(false)
                    Toast.makeText(this, getString(R.string.deleted_from_favorite), Toast.LENGTH_SHORT).show()
                } else {
                    userDetail?.let { detailViewModel.addToFavorite(it) }
                    isFavorite(true)
                    Toast.makeText(this, getString(R.string.added_to_favorite), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        username = null
        profileUrl = null
        isFavorite = null
        super.onDestroy()
    }

    private fun onDetailReceived(result: Result<User>) {
        when (result) {
            is Result.Loading -> showLoading(true)
            is Result.Error -> {
                errorOccurred()
                showLoading(false)
                Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
            }
            is Result.Success -> {
                result.data.let { user ->
                    parseUserDetail(user)

                    val userEntity = UserEntity(
                        user.login,
                        user.avatarUrl,
                        true
                    )
                    userDetail = userEntity
                    profileUrl = user.htmlUrl
                }
                showLoading(false)
            }
        }
    }

    private fun isFavorite(favorite: Boolean) {
        if (favorite) {
            binding.fabFavorite.setImageResource(R.drawable.heart_fill)
        } else {
            binding.fabFavorite.setImageResource(R.drawable.heart)
        }
    }

    private fun errorOccurred() {
        binding.apply {
            userDetailContainer.visibility = View.INVISIBLE
            tabs.visibility = View.INVISIBLE
            viewPager.visibility = View.INVISIBLE
        }
    }

    private fun setToolbar(title: String) {
        setSupportActionBar(binding.toolbarDetail)
        binding.collapsingToolbar.isTitleEnabled = false
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            this.title = title
        }
    }

    private fun setViewPager() {
        val viewPager: ViewPager2 = binding.viewPager
        val tabs: TabLayout = binding.tabs

        viewPager.adapter = PagerAdapter(this, username!!)

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.apply {
                pbLoading.visibility = View.VISIBLE
                appBarLayout.visibility = View.INVISIBLE
                viewPager.visibility = View.INVISIBLE
                fabFavorite.visibility = View.GONE
            }
        } else {
            binding.apply {
                pbLoading.visibility = View.GONE
                appBarLayout.visibility = View.VISIBLE
                viewPager.visibility = View.VISIBLE
                fabFavorite.visibility = View.VISIBLE
            }
        }
    }

    private fun parseUserDetail(user: User) {
        binding.apply {
            tvUsername.text = user.login
            tvRepositories.text = user.publicRepos.toString()
            tvGists.text = user.publicGists.toString()
            tvFollowers.text = user.followers.toString()
            tvFollowing.text = user.following.toString()

            tvName.setAndVisible(user.name)
            tvBio.setAndVisible(user.bio)
            tvCompany.setAndVisible(user.company)
            tvLocation.setAndVisible(user.location)
            tvBlog.setAndVisible(user.blog)
            ivProfile.setImageGlide(this@DetailUserActivity, user.avatarUrl)
        }
    }

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
        private val TAB_TITLES = intArrayOf(R.string.followers, R.string.following)
    }
}