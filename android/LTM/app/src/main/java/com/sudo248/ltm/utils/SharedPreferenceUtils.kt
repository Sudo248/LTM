package com.sudo248.ltm.utils

import android.content.Context
import android.content.SharedPreferences
import com.sudo248.ltm.ktx.applicationName
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * # Created by
 * @author **Sudo248**
 * @since 14:13 - 14/08/2022
 */
object SharedPreferenceUtils {
    /**
     * Application share preference
     */
    private lateinit var applicationPref: SharedPreferences

    /**
     * Other Shared preference
     */
    private var pref: SharedPreferences? = null

    /**
     * Share preference is synchronous itself,
     *
     * but we need mutex for **Coroutine**
     */
    private val prefMutex: Mutex by lazy {
        Mutex()
    }

    /**
     * Set default value for each Type
     */
    var defaultIntValue: Int? = null
    var defaultLongValue: Long? = null
    var defaultFloatValue: Float? = null
    var defaultStringValue: String? = null

    /**
     * Create a **[SharedPreferences]**
     *
     * Should call in **Application**
     *
     * By default preference name is application name
     *
     * @param context [Context]
     * @param namePref [String]: application name as default
     */
    fun createApplicationSharePreference(context: Context, namePref: String = context.applicationName) {
        applicationPref = context.getSharedPreferences(namePref, Context.MODE_PRIVATE)
    }

    /**
     * [getSharedPreferences] by name
     * @param namePref [String]
     *
     * @return Share preference by name
     *
     */
    fun getSharedPreferences(context: Context, namePref: String): SharedPreferences {
        pref = context.getSharedPreferences(namePref, Context.MODE_PRIVATE)
        return pref!!
    }

    /**
     *
     */
    fun withSharedPreference(context: Context, namePref: String): SharedPreferenceUtils {
        pref = context.getSharedPreferences(namePref, Context.MODE_PRIVATE)
        return this
    }

    /**
     * SharePreference is synchronous itself
     *
     * **Note**: when two editors are modifying preferences at the same time,
     * the last one to call commit wins.
     *
     * @param transform [SharedPreferences.Editor.() -> Unit]
     */
    private fun edit(
        commit: Boolean = false,
        transform: SharedPreferences.Editor.() -> Unit
    ): SharedPreferenceUtils {
        if (pref == null) {
            val editor = applicationPref.edit()
            transform(applicationPref.edit())
            if(commit) editor.commit() else editor.apply()
        } else {
            transform(pref!!.edit())
        }
        return this
    }

    /**
     * Using [Mutex] to lock [SharedPreferences]
     *
     * **Note**: when two editors are modifying preferences at the same time,
     * the second will be suspended while the first lock preferences.
     *
     * @param transform [SharedPreferences.Editor.() -> Unit]
     */
    private suspend fun editAsync(
        transform: SharedPreferences.Editor.() -> Unit
    ): SharedPreferenceUtils {
        prefMutex.withLock {
            if (pref == null) {
                val editor = applicationPref.edit()
                transform(applicationPref.edit())
                editor.apply()
            } else {
                transform(pref!!.edit())
            }
        }
        return this
    }

    fun commit() {
        pref?.edit()?.commit()
        pref = null
    }

    fun apply() {
        pref?.edit()?.apply()
        pref = null
    }

    fun put(key: String, value: Any): SharedPreferenceUtils = when(value) {
            is Boolean -> putBoolean(key, value)
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            else -> {
                if (value is Set<*> && value.first() is String) {
                    putStringSet(key, value as Set<String>)
                }
                else throw Exception("Shared preference not support for ${value::class.java.simpleName}")
            }
        }

    suspend fun putAsync(key: String, value: Any): SharedPreferenceUtils = when(value) {
        is Boolean -> putBooleanAsync(key, value)
        is String -> putStringAsync(key, value)
        is Int -> putIntAsync(key, value)
        is Long -> putLongAsync(key, value)
        is Float -> putFloatAsync(key, value)
        else -> {
            if (value is Set<*>) {
                if (value.first() is String) {
                    putStringSetAsync(key, value as Set<String>)
                } else {
                    throw Exception(
                        "Shared preference not support for Set of ${value.first()!!::class.java.simpleName}"
                    )
                }
            } else throw Exception("Shared preference not support for ${value::class.java.simpleName}")
        }
    }

    fun putString(key: String, value: String) = edit { putString(key, value) }

    fun getString(key: String, defValue: String = defaultStringValue ?: ""): String =
        (pref ?: applicationPref).getString(key, defValue)!!

    fun putInt(key: String, value: Int) = edit { putInt(key, value) }

    fun getInt(key: String, defValue: Int = defaultIntValue ?: -1) =
        (pref ?: applicationPref).getInt(key, defValue)

    fun putLong(key: String, value: Long) = edit { putLong(key, value) }

    fun getLong(key: String, defValue: Long = defaultLongValue ?: -1) =
        (pref ?: applicationPref).getLong(key, defValue)

    fun putFloat(key: String, value: Float) = edit { putFloat(key, value) }

    fun getFloat(key: String, defValue: Float = defaultFloatValue ?: -1.0f) =
        (pref ?: applicationPref).getFloat(key, defValue)

    fun putBoolean(key: String, value: Boolean) = edit { putBoolean(key, value) }

    fun getBoolean(key: String, defValue: Boolean = false) =
        (pref ?: applicationPref).getBoolean(key, defValue)

    fun putStringSet(key: String, values: Set<String>) = edit { putStringSet(key, values) }

    fun getStringSet(key: String, defValue: Set<String> = emptySet()): Set<String> =
        (pref ?: applicationPref).getStringSet(key, defValue)!!

    suspend fun putStringAsync(key: String, value: String) = editAsync { putString(key, value) }

    suspend fun getStringAsync(key: String, defValue: String = defaultStringValue ?: ""): String =
        prefMutex.withLock {
            getString(key, defValue)
        }

    suspend fun putIntAsync(key: String, value: Int) = editAsync { putInt(key, value) }

    suspend fun getIntAsync(key: String, defValue: Int = defaultIntValue ?: -1) =
        prefMutex.withLock {
            getInt(key, defValue)
        }

    suspend fun putLongAsync(key: String, value: Long) = editAsync { putLong(key, value) }

    suspend fun getLongAsync(key: String, defValue: Long = defaultLongValue ?: -1) =
        prefMutex.withLock {
            getLong(key, defValue)
        }

    suspend fun putFloatAsync(key: String, value: Float) = editAsync { putFloat(key, value) }

    suspend fun getFloatAsync(key: String, defValue: Float = defaultFloatValue ?: -1.0f) =
        prefMutex.withLock {
            getFloat(key, defValue)
        }

    suspend fun putBooleanAsync(key: String, value: Boolean) = editAsync { putBoolean(key, value) }

    suspend fun getBooleanAsync(key: String, defValue: Boolean = false) = prefMutex.withLock {
        getBoolean(key, defValue)
    }

    suspend fun putStringSetAsync(key: String, values: Set<String>) =
        editAsync { putStringSet(key, values) }

    suspend fun getStringSetAsync(key: String, defValue: Set<String> = emptySet()): Set<String> =
        prefMutex.withLock {
            getStringSet(key, defValue)
        }
}