package me.sunnydaydev.mvvmkit.util

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 18.08.2018.
 * mail: mail@sunnydaydev.me
 */

internal class OperationNotSupportedError: Throwable("Operation not supported.")
 
internal fun notSupportedOperation(): Nothing = throw OperationNotSupportedError()