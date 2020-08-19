package co.uk.thewirelessguy.moovtech.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import co.uk.thewirelessguy.moovtech.bluetooth.WriteResponse
import co.uk.thewirelessguy.moovtech.databinding.ActivityDeviceInfoBinding
import co.uk.thewirelessguy.moovtech.extension.setupActionBar
import co.uk.thewirelessguy.moovtech.extension.toast
import com.veepoo.protocol.VPOperateManager
import com.veepoo.protocol.listener.data.IBPSettingDataListener
import com.veepoo.protocol.listener.data.IDeviceControlPhoneModelState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DeviceInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceInfoBinding
    private lateinit var deviceAddress: String
    private var writeResponse: WriteResponse = WriteResponse()
    private var isInPttModel = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar(binding.toolbar) {
            title = "Moovtech technical test"
            setDisplayHomeAsUpEnabled(true)
        }

        // Get intent data from previous activity
        deviceAddress = intent.getStringExtra("deviceaddress").toString()
        val deviceName = intent.getStringExtra("deviceName").toString()

        binding.contentWrapper.apply {

            deviceAddressTV.text = "$deviceName - $deviceAddress"

            updateDataButton.setOnClickListener {
                /*VPOperateManager.getMangerInstance(applicationContext)
                    .startDetectBP(writeResponse,
                        IBPDetectDataListener { bpData ->
                            val message =
                                "BpData date statues:\n$bpData"
                            Timber.d(message)
                            toast(message, 1)
                        }, EBPDetectModel.DETECT_MODEL_PUBLIC
                    )*/
                /*VPOperateManager.getMangerInstance(applicationContext)
                    .stopDetectBP(writeResponse, EBPDetectModel.DETECT_MODEL_PUBLIC)*/
                /*VPOperateManager.getMangerInstance(this@DeviceInfoActivity)
                    .readBattery(writeResponse,
                        IBatteryDataListener { batteryData ->
                            val message =
                                """
                                Battery grade: ${batteryData.batteryLevel}
                                Power: ${batteryData.batteryLevel * 25}%
                                """.trimIndent()
                            Timber.d(message)
                            toast(message)
                        })*/
                /*VPOperateManager.getMangerInstance(applicationContext)
                    .readAlarm(writeResponse,
                        IAlarmDataListener { alarmData ->
                            val message = "Read alarm: alarmData"
                            Timber.d(message)
                            toast(message)
                        })*/
                /*VPOperateManager.getMangerInstance(applicationContext)
                    .startDetectHeart(writeResponse,
                        IHeartDataListener { heart ->
                            val message = "heart:\n$heart"
                            Timber.d(message)
                            //sendMsg(message, 1)
                            heartDetectTextView.text = message
                        })*/
                /*VPOperateManager.getMangerInstance(applicationContext)
                    .readLowPower(writeResponse,
                        ILowPowerListener { lowPowerData ->
                            val message =
                                "onLowpowerDataDataChange read:\n$lowPowerData"
                            Timber.d(message)
                            lowPowerTextView.text = message
                        })*/
                /*VPOperateManager.getMangerInstance(applicationContext)
                    .readDetectBP(writeResponse,
                        IBPSettingDataListener { bpSettingData ->
                            val message =
                                "BpSettingData:\n$bpSettingData"
                            Timber.d(message)
                            toast(message)
                        })*/
                VPOperateManager.getMangerInstance(applicationContext).readDetectBP(writeResponse,
                    IBPSettingDataListener { bpSettingData ->
                        val message = "BpSettingData: \n$bpSettingData"
                        Timber.d(message)
                        toast(message)
                    })
            }


        }

        listenDeviceCallbackData()
    }

    /**
     *
     * Before password verification, call this method
     * Because after password verification，inPttModel/outPttModel One of them will have a callback
     */
    private fun listenDeviceCallbackData() {
        Timber.d("listenDeviceCallbackData")
        VPOperateManager.getMangerInstance(applicationContext)
            .settingDeviceControlPhone(object : IDeviceControlPhoneModelState {
                override fun knocknotify(type: Int) {}
                override fun nextMusic() {}
                override fun previousMusic() {}
                override fun pauseAndPlayMusic() {}
                override fun rejectPhone() {}
                override fun cliencePhone() {}
                override fun inPttModel() {
                    Timber.d("inPttModel")
                    isInPttModel = true
                }

                override fun outPttModel() {
                    Timber.d("outPttModel")
                    isInPttModel = false
                }

                override fun sos() {}
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}