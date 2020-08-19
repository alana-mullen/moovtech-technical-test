package co.uk.thewirelessguy.moovtech.bluetooth

import com.veepoo.protocol.listener.base.IBleWriteResponse
import timber.log.Timber

internal class WriteResponse : IBleWriteResponse {
    override fun onResponse(code: Int) {
        Timber.d("write cmd status: $code")
    }
}