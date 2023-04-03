package com.ihdyo.githubuser.ui.view

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
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
                        showSearchingResult(result)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.home_menu, menu)

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
            R.id.favorite -> {
                Intent(this@MainActivity, FavoriteActivity::class.java).also {
                    startActivity(it)
                }
            }
            R.id.setting -> {
                Intent(this@MainActivity, SettingActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun errorOccurred() {
        Toast.makeText(this@MainActivity, "An Error is Occurred", Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pbLoading.visibility = View.VISIBLE
            binding.rvUsers.visibility = View.GONE
        } else {
            binding.pbLoading.visibility = View.GONE
            binding.rvUsers.visibility = View.VISIBLE
        }
    }

    private fun showSearchingResult(result: Result<ArrayList<SimpleUser>>) {
        when (result) {
            is Result.Loading -> showLoading(true)
            is Result.Error -> {
                errorOccurred()
                showLoading(false)
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
                        goToDetailUser(user)
                    }

                })
                showLoading(false)
            }
        }
    }

    private fun goToDetailUser(user: SimpleUser) {
        Intent(this@MainActivity, DetailUserActivity::class.java).apply {
            putExtra(EXTRA_DETAIL, user.login)
        }.also {
            startActivity(it)
        }
    }
}