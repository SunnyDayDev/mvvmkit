package me.sunnydaydev.mvvmkit.binding

import android.view.View
import androidx.annotation.IdRes
import androidx.databinding.adapters.ListenerUtil

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 23.08.2018.
 * mail: mail@sunnydaydev.me
 */

object BindingsUtil {

    @JvmStatic
    fun <T> listOf(vararg items: T): List<T> = items.toList()

    @JvmStatic
    fun <T> arrayOf(vararg items: T): Array<out T> = items

    @JvmStatic
    fun <F, S> pair(first: F, second: S): Pair<F, S> = Pair(first, second)

}

open class Bindings {

    fun <T: Any> View.getOrTrackListener(@IdRes id: Int, creator: () -> T): T =
            getListener(id) ?: creator().also { trackListener(id, it) }

    fun <T: Any> View.getOrTrackListener(@IdRes id: Int,
                                         checkCurrentFits: (T) -> Boolean,
                                         creator: () -> T): T =
            getListener<T>(id)?.takeIf(checkCurrentFits)
                    ?: creator().also { trackListener(id, it) }

    fun <T: Any> View.trackListener(@IdRes id: Int, listener: T): T? {
        val current = getListener<T>(id)
        if (current === listener) return null
        ListenerUtil.trackListener(this, listener, id)
        return current
    }

    fun <T: Any> View.getListener(@IdRes id: Int): T? =
            ListenerUtil.getListener<T>(this, id)

}