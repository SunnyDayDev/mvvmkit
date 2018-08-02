package me.sunnydaydev.mvvmkit.util

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.content.ContextWrapper



/**
 * Created by sunny on 11.06.2018.
 * mail: mail@sunnydaydev.me
 */

fun <V: ViewDataBinding> Activity.setContentBinding(
        @LayoutRes layoutId: Int
): V = DataBindingUtil.setContentView(this, layoutId)

fun Activity.findViewWithTag(id: Int, value: Any) : View? {
    val contentView = contentView ?: return null
    return when {
        contentView is ViewGroup -> contentView.findViewWithTag(id, value)
        contentView.getTag(id) == value -> contentView
        else -> null
    }
}

val Activity.contentView: View? get() = findViewById<ViewGroup>(android.R.id.content)
        .getChildAt(0)

fun View.findActivity(): Activity? {
    var context = context
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}