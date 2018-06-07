package me.sunnydaydev.mvvmkit.util

import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * Created by sunny on 30.05.2018.
 * mail: mail@sunnydaydev.me
 */

fun <T: ViewDataBinding> LayoutInflater.inflateBinding(
        @LayoutRes layoutId: Int,
        container: ViewGroup?,
        attachToParent: Boolean = false
): T = DataBindingUtil.inflate(this, layoutId, container, attachToParent)

fun <T: ViewDataBinding> LayoutInflater.inflateBinding(
        @LayoutRes layoutId: Int,
        container: ViewGroup?,
        attachToParent: Boolean = false,
        component: DataBindingComponent
): T = DataBindingUtil.inflate(this, layoutId, container, attachToParent, component)