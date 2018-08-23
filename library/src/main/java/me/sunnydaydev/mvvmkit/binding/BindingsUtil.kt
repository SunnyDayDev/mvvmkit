package me.sunnydaydev.mvvmkit.binding

import android.content.Context
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

    companion object {

        @JvmStatic
        protected fun <T: Any> View.getOrTrackListener(@IdRes id: Int, creator: () -> T): T =
                ListenerUtil.getListener<T>(this, id) ?: creator().also {
                    ListenerUtil.trackListener(this, it, id)
                }

    }

}

interface WithContextProvider<T> {

    operator fun invoke(context: Context): T

}