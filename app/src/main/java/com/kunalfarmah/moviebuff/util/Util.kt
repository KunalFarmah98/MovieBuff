package com.kunalfarmah.moviebuff.util

import android.content.Context
import android.net.ConnectivityManager
import com.kunalfarmah.moviebuff.MoviesApplication.Companion.context

class Util {
    companion object {
        fun isNetworkAvailable(): Boolean {
            val connectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }

}