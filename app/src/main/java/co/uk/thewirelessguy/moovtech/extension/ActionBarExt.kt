package co.uk.thewirelessguy.moovtech.extension

import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/**
 * Setup actionbar
 */
fun AppCompatActivity.setupActionBar(toolbarId: Toolbar, action: ActionBar.() -> Unit) {
    setSupportActionBar(toolbarId)
    supportActionBar?.run {
        action()
    }
}

fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run {
        action()
    }
}