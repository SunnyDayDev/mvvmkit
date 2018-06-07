package me.sunnydaydev.mvvmkit.util

import android.app.Activity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.annotation.LayoutRes

/**
 * Created by sunny on 24.05.2018.
 * mail: mail@sunnydaydev.me
 */

fun <V: ViewDataBinding> Activity.setContentBinding(
        @LayoutRes layoutId: Int
): V = DataBindingUtil.setContentView(this, layoutId)