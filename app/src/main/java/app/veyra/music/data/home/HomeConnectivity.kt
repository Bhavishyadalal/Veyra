package app.veyra.music.data.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** One lifecycle-bound network observer; never polls or wakes the radio. */
internal class HomeConnectivity(context: Context) : AutoCloseable {
    private val manager = context.getSystemService(ConnectivityManager::class.java)
    private fun connected(): Boolean = runCatching {
        val cm = manager ?: return@runCatching true // Unknown device support: let a real request decide.
        val network = cm.activeNetwork ?: return@runCatching false
        val caps = cm.getNetworkCapabilities(network) ?: return@runCatching false
        caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }.getOrDefault(true)
    private val _online = MutableStateFlow(connected())
    val online = _online.asStateFlow()
    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) { _online.value = connected() }
        override fun onLost(network: Network) { _online.value = connected() }
        override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) { _online.value = connected() }
    }
    init { runCatching { manager?.registerDefaultNetworkCallback(callback) } }
    override fun close() { runCatching { manager?.unregisterNetworkCallback(callback) } }
}
