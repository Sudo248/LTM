package com.sudo248.ltm.ui.activity.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
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
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHost = supportFragmentManager.findFragmentById(R.id.fcvMain) as NavHostFragment
        NavigationUI.setupWithNavController(binding.navMain, navHost.navController)
        Log.d("sudoo", "onCreate: MainActibity")
//        viewModel.updateConversation()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkConnect()
    }

    fun hideBottomNavigation() {
        binding.navMain.gone()
    }

    fun showBottomNavigation() {
        binding.navMain.visible()
    }
}