package me.sunnydaydev.mvvmkit.util

import java.io.Closeable

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 17.08.2018.
 * mail: mail@sunnydaydev.me
 */
 
interface OnDemandSource<T>: Closeable {

    val size: Int

    operator fun get(index: Int): T

}