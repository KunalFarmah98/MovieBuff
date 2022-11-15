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
import com.kunalfarmah.moviebuff.listener.DatastorePreferencesChangedListener
import com.kunalfarmah.moviebuff.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "appDatastore",
    produceMigrations = { context -> listOf(SharedPreferencesMigration(context, Constants.PREFS_NAME)) }
)

object PreferenceManager {
    private val preferencesDataStore = context?.dataStore
    private val TAG = "PreferenceManager"
    var listener : DatastorePreferencesChangedListener? = null

    fun putValue(key: String, value: Any?) {
       CoroutineScope(Dispatchers.Default).launch {
           put(key, value)
       }
    }

    private suspend fun put(key: String, value: Any?) {
        Timber.d("$TAG: Setting $key : $value")
        when (value) {
            is Int -> preferencesDataStore?.edit {
                    it[intPreferencesKey(key)] = value as Int
                    listener?.onDataStorePreferencesChanged(key, value as Int)
                }
            is Float -> preferencesDataStore?.edit {
                    it[floatPreferencesKey(key)] = value as Float
                    listener?.onDataStorePreferencesChanged(key, value as Float)
            }
            is String -> preferencesDataStore?.edit {
                    it[stringPreferencesKey(key)] = value as String
                    listener?.onDataStorePreferencesChanged(key, value  as String)
                }
            is Boolean -> preferencesDataStore?.edit {
                    it[booleanPreferencesKey(key)] = value as Boolean
                    listener?.onDataStorePreferencesChanged(key, value as Boolean)
                }
            is Long -> preferencesDataStore?.edit {
                    it[longPreferencesKey(key)] = value as Long
                    listener?.onDataStorePreferencesChanged(key, value as Long)
                }
            is Set<*> -> preferencesDataStore?.edit {
                    it[stringSetPreferencesKey(key)] = value as Set<String>
                    listener?.onDataStorePreferencesChanged(key, value as Set<String>)
                }
            null -> Timber.e("$TAG: Can't set value to null, ignoring")
            else -> {}
        }
    }

    fun getValue(key: String, def: Any?): Any? {
        var log = "Found value for $key"
        var value = runBlocking {
            return@runBlocking preferencesDataStore?.data?.map {
                when (def) {
                    is Int -> it[intPreferencesKey(key)] ?: def
                    is Float ->  it[floatPreferencesKey(key)] ?: def
                    is String -> it[stringPreferencesKey(key)] ?: def
                    is Boolean ->  it[booleanPreferencesKey(key)] ?: def
                    is Long -> it[longPreferencesKey(key)] ?: def
                    is Set<*> -> it[stringSetPreferencesKey(key)] ?: def
                    null ->  def
                    else -> {}
                }
            }?.first()
        }

        log += if (value != null) {
            ": $value"
        } else {
            ": null"
        }
        Timber.d("$TAG: $log")
        return value
    }
}