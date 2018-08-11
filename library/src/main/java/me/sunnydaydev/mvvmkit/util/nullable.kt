package me.sunnydaydev.mvvmkit.util

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 11.08.2018.
 * mail: mail@sunnydaydev.me
 */

inline fun <reified T> isNullable(): Boolean = null is T