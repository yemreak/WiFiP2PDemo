package com.yemreak.wifip2pdemo

import android.Manifest
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

	val PRC_ACCESS_FINE_LOCATION = 1

	/**
	 * WiFi alıcısı için filtreleme
	 */
	private val wifiFilter = IntentFilter().apply {
		addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
		addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
		addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
		addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
	}

	/**
	 * WiFi değişikliklerinde receiver'ı çalıştırma
	 */
	private lateinit var manager: WifiP2pManager

	/**
	 * WiFi P2P Framework'ü ile uygulamamıza bağlanmayı sağlayacak obje
	 */
	private lateinit var channel: WifiP2pManager.Channel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		initWP2P()
	}

	private fun initWP2P() {
		manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
		channel = manager.initialize(this, mainLooper, null)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWifiP2PPermissions()
		}

	}

	@RequiresApi(Build.VERSION_CODES.M)
	private fun getWifiP2PPermissions() {
		if (!hasWifiP2PPermission()) {
			requestWifiP2PPermissions()
		}
	}

	@RequiresApi(Build.VERSION_CODES.M)
	private fun hasWifiP2PPermission(): Boolean {
		return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
	}

	@RequiresApi(Build.VERSION_CODES.M)
	private fun requestWifiP2PPermissions() {
		requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PRC_ACCESS_FINE_LOCATION)
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		when(requestCode) {
			PRC_ACCESS_FINE_LOCATION -> if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(this, "Konum izni gereklidir", Toast.LENGTH_SHORT).show()
				finish()
			}
		}
	}
}
