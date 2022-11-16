package com.kunalfarmah.moviebuff.preferences

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.kunalfarmah.moviebuff.MoviesApplication.Companion.context
import com.kunalfarmah.moviebuff.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

object PreferenceManager {
    val preferences = context?.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
    private val editor: SharedPreferences.Editor? = preferences?.edit()
    private val TAG = "PreferenceManager"

    fun putValue(key: String, value: Any?) {
        Timber.d("$TAG: Setting $key : $value")
        when (value) {
            is Int -> editor?.putInt(key, value as Int)
            is Float -> editor?.putFloat(key, value as Float)
            is String -> editor?.putString(key, value as String)
            is Boolean -> editor?.putBoolean(key, value as Boolean)
            is Long -> editor?.putLong(key, value as Long)
            is Set<*> -> editor?.putStringSet(key, value as Set<String>)
            null -> editor?.putString(key, null)
            else -> {}
        }
        commitAsync()
    }


    fun getValue(key: String, def: Any?): Any? {
        var log = "Found value for $key"
        var value: Any? = null
        when (def) {
            is Int -> value = preferences?.getInt(key, def) as Int
            is Float -> value = preferences?.getFloat(key, def) as Float
            is String -> value = preferences?.getString(key, def) as String
            is Boolean -> value = preferences?.getBoolean(key, def) as Boolean
            is Long -> value = preferences?.getLong(key, def) as Long
            is Set<*> -> value = preferences?.getStringSet(key, def as Set<String>) as Set<String>
            null -> value = preferences?.getString(key, null)
            else -> {}
        }
        log += if (value != null) {
            ": $value"
        } else {
            ": null"
        }
        Timber.d("$TAG: $log")
        return value
    }

    private fun commitAsync(){
        CoroutineScope(Dispatchers.IO).launch {
            editor?.commit()
        }
    }
}