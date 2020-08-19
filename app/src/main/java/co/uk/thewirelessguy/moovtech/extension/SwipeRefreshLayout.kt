package co.uk.thewirelessguy.moovtech.extension

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

fun SwipeRefreshLayout?.start() {
    this?.apply {
        isRefreshing = true
        isEnabled = false
    }
}

fun SwipeRefreshLayout?.stop() {
    this?.apply {
        isRefreshing = false
        isEnabled = true
    }
}