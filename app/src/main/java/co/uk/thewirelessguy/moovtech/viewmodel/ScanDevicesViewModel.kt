package co.uk.thewirelessguy.moovtech.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.uk.thewirelessguy.moovtech.repository.ScanDevicesRepository
import co.uk.thewirelessguy.moovtech.util.State
import com.inuker.bluetooth.library.search.SearchResult
import kotlinx.coroutines.launch

class ScanDevicesViewModel @ViewModelInject constructor(
    private val repository: ScanDevicesRepository
) : ViewModel() {

    // Encapsulate access to mutable LiveData using backing property:
    private val _devicesListLiveData = MutableLiveData<State<MutableList<SearchResult>>>()
    val devicesListLiveData: LiveData<State<MutableList<SearchResult>>> = _devicesListLiveData

    fun getDevicesLiveData() {
        viewModelScope.launch {
            val response = repository.fetchDevices()
            _devicesListLiveData.postValue(response)
        }
    }

}