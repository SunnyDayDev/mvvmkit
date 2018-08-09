package me.sunnydaydev.mvvmkit.util

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 09.08.2018.
 * mail: mail@sunnydaydev.me
 */
 
class LateInitValue<T: Any> {

    internal lateinit var value: T
        private set

    @Synchronized
    fun set(value: T) {
        this.value = value
    }

}

fun <T: Any> lateinit(value: LateInitValue<T>) = lazy { value.value }