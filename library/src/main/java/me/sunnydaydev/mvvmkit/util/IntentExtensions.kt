package me.sunnydaydev.mvvmkit.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent

/**
 * Created by sunny on 28.04.2018.
 * mail: mail@sunnydaydev.me
 */

fun Intent.makeRestartActivityTask(): Intent {

    val result = Intent.makeRestartActivityTask(component)
    result.putExtras(this)
    return result

}

inline fun <reified T: Context> createIntent(pkg: String, config: Intent.() -> Unit = { }) : Intent {
    return Intent()
            .setComponent(componentName(pkg, T::class.java))
            .apply(config)
}

fun componentName(pkg: String, klass: Class<*>): ComponentName =
        ComponentName(pkg, klass.name)