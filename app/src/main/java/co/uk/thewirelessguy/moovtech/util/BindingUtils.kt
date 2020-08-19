package co.uk.thewirelessguy.moovtech.util

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import co.uk.thewirelessguy.moovtech.extension.loadUrl

object BindingUtils {

    @BindingAdapter("bind:imageUrl")
    @JvmStatic
    fun loadImage(imageView: AppCompatImageView, url: String?) {
        url?.let { imageView.loadUrl(it) }
    }

    @BindingAdapter("android:visibility")
    @JvmStatic
    fun setVisibility(view: View, value: Boolean?) {
        view.visibility = if (value!!) View.VISIBLE else View.GONE
    }

}
