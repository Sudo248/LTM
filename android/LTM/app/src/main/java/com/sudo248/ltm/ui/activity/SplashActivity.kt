package com.sudo248.ltm.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sudo248.ltm.R
import com.sudo248.ltm.api.model.Request
import com.sudo248.ltm.api.model.RequestMethod
import com.sudo248.ltm.ui.activity.auth.AuthActivity
import com.sudo248.ltm.ui.activity.main.MainActivity
import com.sudo248.ltm.websocket.WebSocketService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var socketService: WebSocketService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val jobConnectWs = lifecycleScope.launch(Dispatchers.IO) {
            if (!socketService.isOpen) {
                socketService.connect()
            }
        }

        findViewById<LinearLayout>(R.id.ln_let_go).setOnClickListener {
            lifecycleScope.launch {
                jobConnectWs.join()
                nextActivity()
            }
        }

        findViewById<FloatingActionButton>(R.id.fab_let_go).setOnClickListener {
            nextActivity()
        }
    }

    private fun nextActivity() {
        startActivity(Intent(this, MainActivity::class.java))
//        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}