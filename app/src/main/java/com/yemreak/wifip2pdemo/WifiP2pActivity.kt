package com.yemreak.wifip2pdemo

import android.Manifest
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_wifip2p.*

class WifiP2pActivity : AppCompatActivity() {

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

	private lateinit var wifiReceiver: WifiP2PBroadcastReceiver

	/**
	 * WiFi P2P aktiflik durumu
	 */
	var p2pEnable: Boolean = false
		set(value) {
			field = value
			tvP2pStatus.text = value.toString()
		}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_wifip2p)

		initWP2P()
	}

    /**
     * Eşleşilebilir cihazların listesi
     */
    val peerList = ArrayList<WifiP2pDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifip2p)

        initWP2P()
    }

    private fun initWP2P() {
        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWifiP2PPermissions()
        }

        wifiReceiver = WifiP2PBroadcastReceiver(manager, channel, this)

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
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PRC_ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PRC_ACCESS_FINE_LOCATION -> if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Konum izni gereklidir", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun registerWifiReceiver() {
        registerReceiver(wifiReceiver, wifiFilter)
    }

    private fun unregisterWifiReceiver() {
        unregisterReceiver(wifiReceiver)
    }

    override fun onResume() {
        super.onResume()
        registerWifiReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterWifiReceiver()
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun onDiscoverButtonClicked(view: View) {
        Log.d(TAG, "onDiscoverButtonClicked: Butona tıklandı")

        manager.discoverPeers(channel, P2pActionListener("Keşif"))
    }

    fun onPeerAvailable(peerList: WifiP2pDeviceList) {
        peerList.apply {
            Log.v(TAG, "onPeersAvailable: $deviceList")

            this@WifiP2pActivity.peerList.apply {
                if (this != deviceList) {
                    clear()
                    addAll(deviceList)
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun connectPeer(peer: WifiP2pDevice) {
        val config = WifiP2pConfig().apply {
            deviceAddress = peer.deviceAddress
        }

        manager.connect(channel, config, P2pActionListener("Bağlantı"))
    }

    class P2pActionListener(private val purpose: String) : WifiP2pManager.ActionListener {
        override fun onSuccess() {
            Log.d(TAG, "onSuccess: $purpose başarılı")
        }

        override fun onFailure(reason: Int) {
            val reasonMsg = when (reason) {
                WifiP2pManager.P2P_UNSUPPORTED -> "P2P desteklenmiyor"
                WifiP2pManager.ERROR -> "hata oluştur"
                WifiP2pManager.BUSY -> "cihaz başka bir bağlantı ile meşgul"
                else -> ""
            }

            Log.e(TAG, "onDiscoverButtonClick: $purpose başarısız, $reasonMsg")
        }
    }
}
