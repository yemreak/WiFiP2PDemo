package com.yemreak.wifip2pdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log

class WifiP2PBroadcastReceiver(
    val manager: WifiP2pManager,
    val channel: WifiP2pManager.Channel,
    val wifiActivity: MainActivity
) : BroadcastReceiver() {

    companion object {
        val TAG = WifiP2PBroadcastReceiver::javaClass.name
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> onStateChanged()
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> onPeerChanged()
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION ->
                onConnectionChanged()
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION ->
                onThisDeviceChanged()
        }
    }

    private fun onStateChanged(): Unit {
        Log.d(TAG, "onStateChanged: ")
    }

    private fun onPeerChanged(): Unit {
        Log.d(TAG, "onPeerChanged: ")
    }

    private fun onConnectionChanged(): Unit {
        Log.d(TAG, "onConnectionChanged: ")
    }

    private fun onThisDeviceChanged(): Unit {
        Log.d(TAG, "onThisDeviceChanged: ")
    }

}