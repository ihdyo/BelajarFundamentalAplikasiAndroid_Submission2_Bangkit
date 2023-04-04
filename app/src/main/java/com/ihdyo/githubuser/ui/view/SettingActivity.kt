package com.ihdyo.githubuser.ui.view

import android.os.Bundle
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ihdyo.githubuser.R
import com.ihdyo.githubuser.databinding.ActivitySettingBinding
import com.ihdyo.githubuser.ui.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SettingActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    private lateinit var binding: ActivitySettingBinding

    private val settingViewModel: SettingViewModel by viewModels ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarSetting)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            this.title = getString(R.string.setting)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    settingViewModel.getTheme.collect { state ->
                        if (state) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                        binding.switchDarkMode.isChecked = state
                    }
                }
            }
        }

        binding.switchDarkMode.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.switch_dark_mode -> settingViewModel.saveThemeSetting(isChecked)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}