package com.yemreak.wifip2pdemo

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import androidx.annotation.RequiresPermission

class WifiP2PBroadcastReceiver(
		val manager: WifiP2pManager,
		val channel: WifiP2pManager.Channel,
		val wifiP2pActivity: WifiP2pActivity
) : BroadcastReceiver() {

	companion object {
		val TAG = WifiP2PBroadcastReceiver::javaClass.name
	}

	override fun onReceive(context: Context, intent: Intent) {
		when (intent.action) {
			WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> onStateChanged(intent)
			WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> onPeerChanged()
			WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION ->
				onConnectionChanged()
			WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION ->
				onThisDeviceChanged()
		}
	}

	/**
	 * Wifi P2P durum değişikliklerinde tetiklenir
	 */
	private fun onStateChanged(intent: Intent): Unit {
		Log.d(TAG, "onStateChanged: Wifi P2P durumu değişti")

		wifiP2pActivity.p2pEnable = when (
			intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
			) {
			WifiP2pManager.WIFI_P2P_STATE_ENABLED -> true
			else -> false
		}
	}

	@RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
	private fun onPeerChanged(): Unit {
        Log.d(TAG, "onPeerChanged: WiFi eşleri değişti")

		manager.requestPeers(channel, wifiP2pActivity::onPeerAvailable)
	}

	private fun onConnectionChanged(): Unit {
        Log.d(TAG, "onConnectionChanged: WiFi P2P bağlantısı değişti")
	}

	private fun onThisDeviceChanged(): Unit {
		Log.d(TAG, "onThisDeviceChanged: ")
	}

}
