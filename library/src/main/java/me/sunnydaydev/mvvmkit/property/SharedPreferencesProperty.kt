package me.sunnydaydev.mvvmkit.property

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 16.08.2018.
 * mail: mail@sunnydaydev.me
 */

inline fun <reified T: Boolean?> prefsBool(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = preferenceProperty<T, Boolean>(
        name = name,
        defaultValue = defaultValue,
        set = { editor -> { key, value -> editor.putBoolean(key, value) } },
        get = { preference -> { key -> preference.getBoolean(key, false) } }
)

inline fun <reified T: Int?> prefsInt(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = preferenceProperty<T, Int>(
        name = name,
        defaultValue = defaultValue,
        set = { editor -> { key, value -> editor.putInt(key, value) } },
        get = { preference -> { key -> preference.getInt(key, 0) } }
)

inline fun <reified T: Long?> prefsLong(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = preferenceProperty<T, Long>(
        name = name,
        defaultValue = defaultValue,
        set = { editor -> { key, value -> editor.putLong(key, value) } },
        get = { preference -> { key -> preference.getLong(key, 0) } }
)

inline fun <reified T: Float?> prefsFloat(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = preferenceProperty<T, Float>(
        name = name,
        defaultValue = defaultValue,
        set = { editor -> { key, value -> editor.putFloat(key, value) } },
        get = { preference -> { key -> preference.getFloat(key, 0.0f) } }
)

inline fun <reified T: String?> perfsString(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = preferenceProperty<T, String>(
        name = name,
        defaultValue = defaultValue,
        set = { editor -> { key, value -> editor.putString(key, value) } },
        get = { preference -> { key -> preference.getString(key, "")!! } }
)

inline fun <reified T: Set<String>?> prefsStringSet(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = preferenceProperty<T, Set<String>>(
        name = name,
        defaultValue = defaultValue,
        set = { editor -> { key, value -> editor.putStringSet(key, value) } },
        get = { preference -> { key -> preference.getStringSet(key, emptySet())!! } }
)

inline fun <reified T: TNN?, TNN: Any> preferenceProperty(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue,
        noinline set: (preference: SharedPreferences.Editor) -> (key: String, value: TNN) -> Unit,
        noinline get: (preference: SharedPreferences) -> (key: String) -> TNN
):ReadWriteProperty<SharedPreferences, T> = SharedPreferencesProperty<T, TNN>(name, defaultValue, set, get)

class SharedPreferencesProperty<T: TNN?, TNN: Any>(
        private val name: String,
        private val defaultValue: () -> T,
        private val setterProvider: (preference: SharedPreferences.Editor) -> (key: String, value: TNN) -> Unit,
        private val getterProvider: (preference: SharedPreferences) -> (key: String) -> TNN
): ReadWriteProperty<SharedPreferences, T> {

    override fun getValue(thisRef: SharedPreferences, property: KProperty<*>): T {

        return if (thisRef.contains(name)) getterProvider(thisRef)(name) as T
        else defaultValue()

    }

    override fun setValue(thisRef: SharedPreferences, property: KProperty<*>, value: T) {

        if (value == null) {
            thisRef.edit().remove(name).apply()
        } else {
            thisRef.edit()
                    .apply{ setterProvider(this)(name, value) }
                    .apply()
        }

    }

}
