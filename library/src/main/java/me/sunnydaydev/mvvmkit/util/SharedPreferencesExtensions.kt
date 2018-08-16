package me.sunnydaydev.mvvmkit.util

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 16.08.2018.
 * mail: mail@sunnydaydev.me
 */

fun SharedPreferences.booleanProperty(
        name: String, defaultValue: Boolean
): ReadWriteProperty<Any, Boolean> = object: PrefsProperty<Boolean>(name, this, defaultValue) {

    override fun get(prefs: SharedPreferences, name: String): Boolean? =
            prefs.getBoolean(name, defaultValue)

    override fun put(prefs: SharedPreferences.Editor, name: String, value: Boolean) =
            prefs.putBoolean(name, value)

}

fun SharedPreferences.intProperty(
        name: String, defaultValue: Int
): ReadWriteProperty<Any, Int> = object: PrefsProperty<Int>(name, this, defaultValue) {

    override fun get(prefs: SharedPreferences, name: String): Int? =
            prefs.getInt(name, defaultValue)

    override fun put(prefs: SharedPreferences.Editor, name: String, value: Int) =
            prefs.putInt(name, value)

}

fun SharedPreferences.longProperty(
        name: String, defaultValue: Long
): ReadWriteProperty<Any, Long> = object: PrefsProperty<Long>(name, this, defaultValue) {

    override fun get(prefs: SharedPreferences, name: String): Long? =
            prefs.getLong(name, defaultValue)

    override fun put(prefs: SharedPreferences.Editor, name: String, value: Long) =
            prefs.putLong(name, value)

}

fun SharedPreferences.floatProperty(
        name: String, defaultValue: Float
): ReadWriteProperty<Any, Float> = object: PrefsProperty<Float>(name, this, defaultValue) {

    override fun get(prefs: SharedPreferences, name: String): Float? =
            prefs.getFloat(name, defaultValue)

    override fun put(prefs: SharedPreferences.Editor, name: String, value: Float) =
            prefs.putFloat(name, value)

}

fun SharedPreferences.stringProperty(
        name: String, defaultValue: String
): ReadWriteProperty<Any, String> = object: PrefsProperty<String>(name, this, defaultValue) {

    override fun get(prefs: SharedPreferences, name: String): String? =
            prefs.getString(name, defaultValue)

    override fun put(prefs: SharedPreferences.Editor, name: String, value: String) =
            prefs.putString(name, value)

}

fun SharedPreferences.stringSetProperty(
        name: String, defaultValue: Set<String>
): ReadWriteProperty<Any, Set<String>> = object: PrefsProperty<Set<String>>(name, this, defaultValue) {

    override fun get(prefs: SharedPreferences, name: String): Set<String>? =
            prefs.getStringSet(name, defaultValue)

    override fun put(prefs: SharedPreferences.Editor, name: String, value: Set<String>) =
            prefs.putStringSet(name, value)

}

abstract class PrefsProperty<T>(
        private val name: String,
        private val prefs: SharedPreferences,
        private val defaultValue: T
): ReadWriteProperty<Any, T> {

    final override fun getValue(thisRef: Any, property: KProperty<*>): T =
            if (prefs.contains(name)) get(prefs, name) ?: defaultValue
            else defaultValue

    final override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        if (value == null) {
            prefs.edit { remove(name) }
        } else {
            prefs.edit { put(this, name, value) }
        }
    }

    abstract fun get(prefs: SharedPreferences, name: String): T?

    abstract fun put(prefs: SharedPreferences.Editor, name: String, value: T): SharedPreferences.Editor

}

