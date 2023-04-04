package com.ihdyo.githubuser.ui.view

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ihdyo.githubuser.R
import com.ihdyo.githubuser.ui.adapter.UserAdapter
import com.ihdyo.githubuser.data.Result
import com.ihdyo.githubuser.data.remote.response.SimpleUser
import com.ihdyo.githubuser.databinding.ActivityMainBinding
import com.ihdyo.githubuser.ui.view.DetailUserActivity.Companion.EXTRA_DETAIL
import com.ihdyo.githubuser.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarHome)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mainViewModel.themeSetting.collect { state ->
                        if (state) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
                launch {
                    mainViewModel.users.collect { result ->
                        when (result) {
                            is Result.Loading -> {
                                binding.pbLoading.visibility = View.VISIBLE
                                binding.rvUsers.visibility = View.GONE
                            }
                            is Result.Error -> {
                                binding.pbLoading.visibility = View.GONE
                                binding.rvUsers.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                val listUserAdapter = UserAdapter(result.data)

                                binding.rvUsers.apply {
                                    layoutManager = LinearLayoutManager(this@MainActivity)
                                    adapter = listUserAdapter
                                    setHasFixedSize(true)
                                }

                                listUserAdapter.setOnItemClickCallback(object :
                                    UserAdapter.OnItemClickCallback {
                                    override fun onItemClicked(user: SimpleUser) {
                                        Intent(this@MainActivity, DetailUserActivity::class.java).apply {
                                            putExtra(EXTRA_DETAIL, user.login)
                                        }.also {
                                            startActivity(it)
                                        }
                                    }

                                })
                                binding.pbLoading.visibility = View.GONE
                                binding.rvUsers.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            queryHint = getString(R.string.github_username)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    mainViewModel.searchByUsername(query ?: "")
                    clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorite -> startActivity(Intent(this@MainActivity, FavoriteActivity::class.java))
            R.id.setting -> startActivity(Intent(this@MainActivity, SettingActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}