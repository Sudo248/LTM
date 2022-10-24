package com.sudo248.ltm.base

import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 09:21 - 23/10/2022
 */
abstract class BaseActivity<out VB : ViewBinding>(private val bindingInflater: (LayoutInflater) -> VB) : AppCompatActivity() {
    private var _binding: VB? = null
    val binding: VB get() = _binding ?: error("Must only access binding while activity onCreate")

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        _binding = bindingInflater(layoutInflater)
    }

}