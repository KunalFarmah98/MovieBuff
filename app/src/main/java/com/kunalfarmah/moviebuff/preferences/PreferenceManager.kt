package com.kunalfarmah.moviebuff.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kunalfarmah.moviebuff.MoviesApplication.Companion.context
import com.kunalfarmah.moviebuff.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "appDatastore",
    produceMigrations = { context -> listOf(SharedPreferencesMigration(context, Constants.PREFS_NAME)) }
)

object PreferenceManager {
    private val preferencesDataStore = context?.dataStore
    private val TAG = "PreferenceManager"

    fun putValue(key: String, value: Any?) {
       CoroutineScope(Dispatchers.Default).launch {
           put(key, value)
       }
    }

    private suspend fun put(key: String, value: Any?) {
        Timber.d("$TAG: Setting $key : $value")
        try {
            preferencesDataStore?.edit {
                when (value) {
                    is Int -> it[intPreferencesKey(key)] = value
                    is Float -> it[floatPreferencesKey(key)] = value
                    is String -> it[stringPreferencesKey(key)] = value
                    is Boolean -> it[booleanPreferencesKey(key)] = value
                    is Long -> it[longPreferencesKey(key)] = value
                    is Set<*> -> it[stringSetPreferencesKey(key)] = value as Set<String>
                    null -> Timber.e("$TAG: Can't set value to null, ignoring")
                    else -> {}
                }
            }
        }
        catch (e:Exception){
            Timber.e("$TAG: Error setting value of $key: ${e.message ?: ""}")
        }
    }

    fun getValue(key: String, def: Any?): Flow<Any?>? {
        var log = "Found value for $key"
        var value = preferencesDataStore?.data?.map {
            when (def) {
                is Int -> it[intPreferencesKey(key)] ?: def
                is Float -> it[floatPreferencesKey(key)] ?: def
                is String -> it[stringPreferencesKey(key)] ?: def
                is Boolean -> it[booleanPreferencesKey(key)] ?: def
                is Long -> it[longPreferencesKey(key)] ?: def
                is Set<*> -> it[stringSetPreferencesKey(key)] ?: def
                null -> def
                else -> {}
            }
        }?.catch {
            Timber.e("$TAG: Error while getting value of $key: ${it.message}")
        }

        CoroutineScope(Dispatchers.IO).launch {
            log += if (value != null) {
                ": ${value.first()}"
            } else {
                ": null"
            }
            Timber.d("$TAG: $log")
        }
        return value
    }
}