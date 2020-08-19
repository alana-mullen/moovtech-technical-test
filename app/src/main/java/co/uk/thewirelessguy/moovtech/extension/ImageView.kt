package co.uk.thewirelessguy.moovtech.extension

import android.widget.ImageView
import coil.api.load

/**
 * Wraps the image loading library with an extension to simplify image loading and to make it
 * easier to swap out libraries at a later date.
 */

fun ImageView.loadUrl(url: String) {
    this.load(url)
}