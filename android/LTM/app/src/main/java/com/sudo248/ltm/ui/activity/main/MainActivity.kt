package com.sudo248.ltm.ui.activity.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.sudo248.ltm.R
import com.sudo248.ltm.databinding.ActivityMainBinding
import com.sudo248.ltm.ktx.gone
import com.sudo248.ltm.ktx.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHost = supportFragmentManager.findFragmentById(R.id.fcvMain) as NavHostFragment
        NavigationUI.setupWithNavController(binding.navMain, navHost.navController)
    }

    fun hideBottomNavigation() {
        binding.navMain.gone()
    }

    fun showBottomNavigation() {
        binding.navMain.visible()
    }
}