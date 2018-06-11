package me.sunnydaydev.mvvmkit.util

import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach

/**
 * Created by sunny on 11.06.2018.
 * mail: mail@sunnydaydev.me
 */

fun ViewGroup.find(check: (View) -> Boolean) : View? {

    if (check(this)) return this

    forEach {
        when {
            it is ViewGroup -> it.find(check)?.also { return it }
            check(it) -> return it
        }
    }

    return null

}

fun ViewGroup.findViewWithTag(id: Int, value: Any) : View? = find {
    it.getTag(id) == value
}