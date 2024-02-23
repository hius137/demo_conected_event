package com.example.demo_conected_event

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class Connectivity(private val connectivityManager: ConnectivityManager) {

    companion object {
        const val CONNECTIVITY_NONE = "none"
        const val CONNECTIVITY_WIFI = "wifi"
        const val CONNECTIVITY_MOBILE = "mobile"
    }

    val networkType: String
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                    ?: return CONNECTIVITY_NONE
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return CONNECTIVITY_WIFI
                }
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return CONNECTIVITY_MOBILE
                }
            }
            return networkTypeLegacy
        }

    @get:Suppress("deprecation")
    private val networkTypeLegacy: String
        private get() {
            // handle type for Android versions less than Android 6
            val info = connectivityManager.activeNetworkInfo
            if (info == null || !info.isConnected) {
                return CONNECTIVITY_NONE
            }
            val type = info.type
            return when (type) {
                ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_WIMAX -> CONNECTIVITY_WIFI
                ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_MOBILE_DUN, ConnectivityManager.TYPE_MOBILE_HIPRI -> CONNECTIVITY_MOBILE
                else -> CONNECTIVITY_NONE
            }
        }

}
