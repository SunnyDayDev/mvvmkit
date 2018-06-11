package me.sunnydaydev.mvvmkit.util

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * Created by sunny on 11.06.2018.
 * mail: mail@sunnydaydev.me
 */

fun <V: ViewDataBinding> Activity.setContentBinding(
        @LayoutRes layoutId: Int
): V = DataBindingUtil.setContentView(this, layoutId)

fun Activity.findViewWithTag(id: Int, value: Any) : View? {
    val root = findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as ViewGroup
    return root.findViewWithTag(id, value)
}