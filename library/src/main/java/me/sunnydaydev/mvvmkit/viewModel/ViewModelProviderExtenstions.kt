package me.sunnydaydev.mvvmkit.viewModel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import kotlin.reflect.KClass

/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnydaydev.me
 */

inline operator fun <reified T: ViewModel> ViewModelProvider.invoke(): T = this[T::class]

operator fun <T: ViewModel> ViewModelProvider.get(clazz: KClass<T>): T = this[clazz.java]