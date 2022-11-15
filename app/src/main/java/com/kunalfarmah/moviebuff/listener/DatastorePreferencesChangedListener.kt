package com.kunalfarmah.moviebuff.listener

interface DatastorePreferencesChangedListener {
    fun onDataStorePreferencesChanged(key: String, value: Any)
}