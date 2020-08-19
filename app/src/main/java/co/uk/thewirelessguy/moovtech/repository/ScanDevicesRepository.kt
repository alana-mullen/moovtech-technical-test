package co.uk.thewirelessguy.moovtech.repository

import android.content.Context
import co.uk.thewirelessguy.moovtech.model.Device
import co.uk.thewirelessguy.moovtech.util.State
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import com.veepoo.protocol.VPOperateManager
import dagger.hilt.android.qualifiers.ActivityContext
import timber.log.Timber
import javax.inject.Inject

class ScanDevicesRepository @Inject constructor(
    @ActivityContext private val context: Context
) {

    private lateinit var mVpoperateManager: VPOperateManager

    /*private val mSearchResponse: SearchResponse = object : SearchResponse {
        override fun onSearchStarted() {
            //Logger.t(MainActivity.TAG).i("onSearchStarted")
        }

        override fun onDeviceFounded(device: SearchResult) {
            *//*Logger.t(MainActivity.TAG).i(
                String.format(
                    "device for %s-%s-%d",
                    device.name,
                    device.address,
                    device.rssi
                )
            )*//*
            *//*runOnUiThread(Runnable {
                if (!mListAddress.contains(device.address)) {
                    mListData.add(device)
                    mListAddress.add(device.address)
                }
                Collections.sort(mListData, DeviceCompare())
                bleConnectAdatpter.notifyDataSetChanged()
            })*//*
        }

        override fun onSearchStopped() {
            //refreshStop()
            //Logger.t(MainActivity.TAG).i("onSearchStopped")
        }

        override fun onSearchCanceled() {
            //refreshStop()
            //Logger.t(MainActivity.TAG).i("onSearchCanceled")
        }
    }*/

    //suspend fun fetchDevices(viewModelCallBack : (State<MutableList<Device>>) -> Unit) {
    suspend fun fetchDevices() : State<MutableList<SearchResult>> {
        Timber.d("fetchDevices")
        val mSearchResponse: SearchResponse = object : SearchResponse {
            override fun onSearchStarted() {
                //Logger.t(MainActivity.TAG).i("onSearchStarted")
                Timber.d("onSearchStarted")
            }

            override fun onDeviceFounded(device: SearchResult) {
                /*Logger.t(MainActivity.TAG).i(
                    String.format(
                        "device for %s-%s-%d",
                        device.name,
                        device.address,
                        device.rssi
                    )
                )*/
                Timber.d(
                    String.format(
                        "device for %s-%s-%d",
                        device.name,
                        device.address,
                        device.rssi
                    )
                )
                /*runOnUiThread(Runnable {
                    if (!mListAddress.contains(device.address)) {
                        mListData.add(device)
                        mListAddress.add(device.address)
                    }
                    Collections.sort(mListData, DeviceCompare())
                    bleConnectAdatpter.notifyDataSetChanged()
                })*/
                //return State.success(mutableListOf<Device>())
            }

            override fun onSearchStopped() {
                //refreshStop()
                //Logger.t(MainActivity.TAG).i("onSearchStopped")
                Timber.d("onSearchStopped")
            }

            override fun onSearchCanceled() {
                //refreshStop()
                //Logger.t(MainActivity.TAG).i("onSearchCanceled")
                Timber.d("onSearchCanceled")
            }
        }

        mVpoperateManager = VPOperateManager.getMangerInstance(context)
        mVpoperateManager.startScanDevice(mSearchResponse)
        mVpoperateManager.stopScanDevice()
        return State.success(mutableListOf<SearchResult>())
    }

}