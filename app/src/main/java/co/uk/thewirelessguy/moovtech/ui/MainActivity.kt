package co.uk.thewirelessguy.moovtech.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import co.uk.thewirelessguy.moovtech.adapter.BleScanViewAdapter
import co.uk.thewirelessguy.moovtech.databinding.ActivityMainBinding
import co.uk.thewirelessguy.moovtech.extension.*
import co.uk.thewirelessguy.moovtech.util.DeviceCompare
import com.inuker.bluetooth.library.Code
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import com.inuker.bluetooth.library.utils.BluetoothUtils
import com.veepoo.protocol.VPOperateManager
import com.veepoo.protocol.listener.base.IABleConnectStatusListener
import com.veepoo.protocol.listener.base.IABluetoothStateListener
import com.veepoo.protocol.util.VPLogger
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityMainBinding
    private val REQUEST_CODE = 1
    private var mListData: MutableList<SearchResult> =
        ArrayList()
    private var mListAddress: MutableList<String> =
        ArrayList()
    private lateinit var bleConnectAdapter: BleScanViewAdapter
    private var mBManager: BluetoothManager? = null
    private var mBAdapter: BluetoothAdapter? = null
    private var mBScanner: BluetoothLeScanner? = null
    private var mVpoperateManager: VPOperateManager? = null
    private var mIsOadModel = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use View Binding to inflate layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar(binding.toolbar) {
            title = "Moovtech technical test"
        }

        binding.mainRecylerlist.apply {
            layoutManager = LinearLayoutManager(context)

            hasFixedSize() // Improve performance (use only with fixed size items)
            setItemViewCacheSize(20)

            bleConnectAdapter = BleScanViewAdapter(context, mListData) {
                Timber.d("Item clicked: %s", it.address)
                toast("Connecting, please wait...")
                connectDevice(it.address, it.name)
            }
            adapter = bleConnectAdapter

            // Set a view to display when list is empty:
            adapter?.setEmptyStateView(binding.deviceListEmpty.root)
        }
        binding.mainSwipeRefreshLayout.setOnRefreshListener(this)

        Timber.d("onSearchStarted")
        mVpoperateManager = VPOperateManager.getMangerInstance(applicationContext)
        VPLogger.setDebug(true)
        checkPermission()
        registerBluetoothStateListener()

        if (checkBLE()) {
            scanDevice()
        }
    }

    private fun checkPermission() {
        Timber.d("Build.VERSION.SDK_INT =%s", Build.VERSION.SDK_INT)
        if (Build.VERSION.SDK_INT <= 22) {
            initBLE()
            return
        }
        val permissionCheck: Int = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Timber.d("checkPermission,PERMISSION_GRANTED")
            initBLE()
        } else if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            requestPermission()
            Timber.d("checkPermission,PERMISSION_DENIED")
        }
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            !== PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                Timber.d("requestPermission,shouldShowRequestPermissionRationale")
            } else {
                Timber.d("requestPermission,shouldShowRequestPermissionRationale else")
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_BLUETOOTH
                )
            }
        } else {
            Timber.d("requestPermission,shouldShowRequestPermissionRationale")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_BLUETOOTH -> {
                Timber.d("onRequestPermissionsResult,MY_PERMISSIONS_REQUEST_BLUETOOTH ")
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    initBLE()
                } else {
                }
                return
            }
        }
    }

    /**
     * Scan for devices and update RecyclerView list
     */
    private fun scanDevice(): Boolean {
        if (mListAddress.isNotEmpty()) {
            mListAddress.clear()
        }
        if (mListData.isNotEmpty()) {
            mListData.clear()
            bleConnectAdapter.notifyDataSetChanged()
        }
        if (!BluetoothUtils.isBluetoothEnabled()) {
            toast("Bluetooth is not turned on")
            return true
        }
        mVpoperateManager!!.startScanDevice(mSearchResponse)
        return false
    }

    /**
     * Connect device and open DeviceInfoActivity
     */
    private fun connectDevice(mac: String, deviceName: String) {
        mVpoperateManager?.apply {
            registerConnectStatusListener(mac, mBleConnectStatusListener)
            connectDevice(mac, deviceName,
                { code, profile, isoadModel ->
                    if (code == Code.REQUEST_SUCCESS) {
                        //Bluetooth and device connection status
                        Timber.d("Connection succeeded")
                        Timber.d("Firmware upgrade mode=$isoadModel")
                        mIsOadModel = isoadModel
                    } else {
                        Timber.d("Connection failed")
                        toast("Connection failed")
                    }
                }) { state ->
                if (state == Code.REQUEST_SUCCESS) {
                    //Bluetooth and device connection status
                    Timber.d("Successful monitoring-other operations can be performed")
                    val intent = Intent(applicationContext, DeviceInfoActivity::class.java)
                    intent.putExtra("isoadmodel", mIsOadModel)
                    intent.putExtra("deviceaddress", mac)
                    intent.putExtra("deviceName", deviceName)
                    startActivity(intent)
                } else {
                    Timber.d("Failed to monitor, reconnect")
                    toast("Failed to monitor, reconnect")
                }
            }
        }
    }

    /**
     * Bluetooth on or off
     */
    private fun registerBluetoothStateListener() {
        mVpoperateManager!!.registerBluetoothStateListener(mBluetoothStateListener)
    }

    /**
     * Monitor the callback status of the system's Bluetooth on and off
     */
    private val mBleConnectStatusListener: IABleConnectStatusListener =
        object : IABleConnectStatusListener() {
            override fun onConnectStatusChanged(
                mac: String,
                status: Int
            ) {
                if (status == Constants.STATUS_CONNECTED) {
                    Timber.d("STATUS_CONNECTED")
                } else if (status == Constants.STATUS_DISCONNECTED) {
                    Timber.d("STATUS_DISCONNECTED")
                }
            }
        }

    /**
     * Monitor the callback status between Bluetooth and the device
     */
    private val mBluetoothStateListener: IABluetoothStateListener =
        object : IABluetoothStateListener() {
            override fun onBluetoothStateChanged(openOrClosed: Boolean) {
                Timber.d("open=$openOrClosed")
            }
        }

    /**
     * Scanned callback
     */
    private val mSearchResponse: SearchResponse = object : SearchResponse {
        override fun onSearchStarted() {
            Timber.d("onSearchStarted")
        }

        override fun onDeviceFounded(device: SearchResult) {
            Timber.d(
                String.format(
                    "device for %s-%s-%d",
                    device.name,
                    device.address,
                    device.rssi
                )
            )
            runOnUiThread {
                if (!mListAddress.contains(device.address)) {
                    mListData.add(device)
                    mListAddress.add(device.address)
                }
                Collections.sort(mListData, DeviceCompare())
                bleConnectAdapter.notifyDataSetChanged()
            }
        }

        override fun onSearchStopped() {
            refreshStop()
            Timber.d("onSearchStopped")
        }

        override fun onSearchCanceled() {
            refreshStop()
            Timber.d("onSearchCanceled")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (BluetoothUtils.isBluetoothEnabled()) {
                scanDevice()
            } else {
                refreshStop()
            }
        }
    }

    override fun onRefresh() {
        Timber.d("onRefresh")
        if (checkBLE()) {
            scanDevice()
        }
    }

    private fun initBLE() {
        mBManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        if (null != mBManager) {
            mBAdapter = mBManager!!.adapter
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBScanner = mBAdapter!!.bluetoothLeScanner
        }
        checkBLE()
    }

    /**
     * Check if the Bluetooth device is turned on
     *
     * @return
     */
    private fun checkBLE(): Boolean {
        return if (!BluetoothUtils.isBluetoothEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_CODE)
            false
        } else {
            true
        }
    }

    /**
     * End refresh
     */
    fun refreshStop() {
        Timber.d("refreshComlete")
        binding.mainSwipeRefreshLayout.stop()
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_BLUETOOTH = 0x55
    }
}
