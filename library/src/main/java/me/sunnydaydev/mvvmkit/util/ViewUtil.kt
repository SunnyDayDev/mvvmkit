package me.sunnydaydev.mvvmkit.util

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.forEach

/**
 * Created by sunny on 11.06.2018.
 * mail: mail@sunnydaydev.me
 */

fun ViewGroup.findView(check: (View) -> Boolean) : View? {

    if (check(this)) return this

    forEach {
        when {
            it is ViewGroup -> return it.findView(check) ?: return@forEach
            check(it) -> return it
        }
    }

    return null

}

fun ViewGroup.findViewWithTag(id: Int, value: Any) : View? = findView {
    it.getTag(id) == value
}

fun ViewGroup.findViewWithTransitionName(value: String) : View? = findView {
    ViewCompat.getTransitionName(this) == value
}