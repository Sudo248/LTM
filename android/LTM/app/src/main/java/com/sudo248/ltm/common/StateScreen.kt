package com.sudo248.ltm.common

/**
 * ## Created by
 * @author **Sudo248**
 * @since 00:00 - 15/08/2022
 */

enum class StateScreen {
    HIDE {
        override fun changeState(newState: StateScreen?): StateScreen = newState ?: RUNNING
    },
    RUNNING {
        override fun changeState(newState: StateScreen?): StateScreen = newState ?: LOADING
    },
    LOADING {
        override fun changeState(newState: StateScreen?): StateScreen = newState ?: RUNNING
    },
    ERROR {
        override fun changeState(newState: StateScreen?): StateScreen = newState ?: RUNNING
    };

    abstract fun changeState(newState: StateScreen? = null): StateScreen

    fun onState(
        hide: () -> Unit = {},
        running: () -> Unit = {},
        loading: () -> Unit = {},
        error: () -> Unit = {}
    ) {
        when (this) {
            RUNNING -> running()
            LOADING -> loading()
            ERROR -> error()
            else -> hide()
        }
    }

}