package co.uk.thewirelessguy.moovtech.ui

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import co.uk.thewirelessguy.moovtech.databinding.FragmentScanDevicesBinding
import co.uk.thewirelessguy.moovtech.extension.setEmptyStateView
import co.uk.thewirelessguy.moovtech.extension.stop
import co.uk.thewirelessguy.moovtech.viewmodel.ScanDevicesViewModel
import com.innfinity.permissionflow.lib.permissionFlow
import com.innfinity.permissionflow.lib.withFragment
import com.innfinity.permissionflow.lib.withPermissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class ScanDevicesDialogFragment : DialogFragment(), SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentScanDevicesBinding? = null
    private val binding get() = _binding!!
    private lateinit var devicesAdapter: ScanDevicesListAdapter
    private val viewModel: ScanDevicesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScanDevicesBinding.inflate(inflater, container, false)

        //setHasOptionsMenu(true)

        binding.deviceList.apply {
            // Set LinearLayoutManager using default vertical list:
            layoutManager = LinearLayoutManager(context)

            hasFixedSize() // Improve performance (use only with fixed size items)
            setItemViewCacheSize(20)

            // Set adapter and initialise with empty list:
            devicesAdapter = ScanDevicesListAdapter(mutableListOf()) {
                // Navigate to Activity and pass Id of clicked item as an Intent extra

            }
            adapter = devicesAdapter

            // Set a view to display when list is empty:
            adapter?.setEmptyStateView(binding.deviceListEmpty.root)
        }

        binding.swipeRefresh.setOnRefreshListener(this)

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        @Nullable savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        // Fetch arguments from bundle and set title
        dialog!!.setTitle("Select your device")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Listen for changes from the LiveData ViewModel. Update view when data is loaded.
        /*viewModel.devicesListLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is State.Success -> {
                    devicesAdapter.setData(it.data.toMutableList())
                }
                is State.Error -> {
                    context?.toast("Error: ${it.message}")
                }
                is State.Loading -> showLoading()
            }
        }*/
        loadData()
    }

    override fun onRefresh() {
        // Handle pull down to refresh
        loadData()
    }

    private fun loadData(searchQuery: String = "") {
        Timber.d("loadData")
        CoroutineScope(Dispatchers.Main).launch {
            permissionFlow {
                withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                withFragment(this@ScanDevicesDialogFragment)
                request().collect { granted: Boolean ->
                    if (granted) {
                        Timber.d("Permission granted")
                        viewModel.getDevicesLiveData()
                        binding.swipeRefresh.stop()
                    } else {
                        Timber.d("Permission NOT granted")
                    }
                }
            }

        }

    }

    private fun showLoading() {
        Timber.d("Loading...")
    }

}