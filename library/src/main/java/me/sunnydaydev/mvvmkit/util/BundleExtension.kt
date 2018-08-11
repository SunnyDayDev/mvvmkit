package me.sunnydaydev.mvvmkit.util

import android.os.Bundle
import android.os.Parcelable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 30.07.2018.
 * mail: mail@sunnydaydev.me
 */

inline fun <reified T: Boolean?> bundleBoolean(
        name: String,
        noinline defaultValue: () -> T = ::bundleCommonDefaultValue
) = bundleProperty<T, Boolean>(
        name = name,
        defaultValue = defaultValue,
        set = { bundle -> { key, value -> bundle.putBoolean(key, value) }  },
        get = { bundle -> { key -> bundle.getBoolean(key) } }
)

inline fun <reified T: Int?> bundleInt(
        name: String,
        noinline defaultValue: () -> T = ::bundleCommonDefaultValue
) = bundleProperty<T, Int>(
        name = name,
        defaultValue = defaultValue,
        set = { bundle -> { key, value -> bundle.putInt(key, value) }  },
        get = { bundle -> { key -> bundle.getInt(key) } }
)

inline fun <reified T: Long?> bundleLong(
        name: String,
        noinline defaultValue: () -> T = ::bundleCommonDefaultValue
) = bundleProperty<T, Long>(
        name = name,
        defaultValue = defaultValue,
        set = { bundle -> { key, value -> bundle.putLong(key, value) }  },
        get = { bundle -> { key -> bundle.getLong(key) } }
)

inline fun <reified T: Float?> bundleFloat(
        name: String,
        noinline defaultValue: () -> T = ::bundleCommonDefaultValue
) = bundleProperty<T, Float>(
        name = name,
        defaultValue = defaultValue,
        set = { bundle -> { key, value -> bundle.putFloat(key, value) }  },
        get = { bundle -> { key -> bundle.getFloat(key) } }
)

inline fun <reified T: Double?> bundleDouble(
        name: String,
        noinline defaultValue: () -> T = ::bundleCommonDefaultValue
) = bundleProperty<T, Double>(
        name = name,
        defaultValue = defaultValue,
        set = { bundle -> { key, value -> bundle.putDouble(key, value) }  },
        get = { bundle -> { key -> bundle.getDouble(key) } }
)

inline fun <reified T: String?> bundleString(
        name: String,
        noinline defaultValue: () -> T = ::bundleCommonDefaultValue
) = bundleProperty<T, String>(
        name = name,
        defaultValue = defaultValue,
        set = { bundle -> { key, value -> bundle.putString(key, value) }  },
        get = { bundle -> { key -> bundle.getString(key)!! } }
)

inline fun <reified T: Parcelable?> bundleParcellable(
        name: String,
        noinline defaultValue: () -> T = ::bundleCommonDefaultValue
) = bundleProperty<T, Parcelable>(
        name = name,
        defaultValue = defaultValue,
        set = { bundle -> { key, value -> bundle.putParcelable(key, value) }  },
        get = { bundle -> { key -> bundle.getParcelable(key) } }
)

inline fun <reified T: TNN?, TNN: Any> bundleProperty(
        name: String,
        noinline defaultValue: () -> T = ::bundleCommonDefaultValue,
        noinline set: (bundle: Bundle) -> (key: String, value: TNN) -> Unit,
        noinline get: (bundle: Bundle) -> (key: String) -> TNN
):ReadWriteProperty<Bundle, T> = BundleProperty<T, TNN>(name, defaultValue, set, get)

class BundleProperty<T: TNN?, TNN: Any>(
        private val name: String,
        private val defaultValue: () -> T,
        private var setterProvider: ((bundle: Bundle) -> (key: String, value: TNN) -> Unit)?,
        private var getterProvider: ((bundle: Bundle) -> (key: String) -> TNN)?
): ReadWriteProperty<Bundle, T> {

    private var initialized = false

    private lateinit var getter: (key: String) -> TNN
    private lateinit var setter: (key: String, value: TNN) -> Unit

    override fun getValue(thisRef: Bundle, property: KProperty<*>): T {

        init(thisRef)

        return if (thisRef.containsKey(name)) getter(name) as T
               else defaultValue()

    }

    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: T) {

        init(thisRef)

        if (value == null) {
            thisRef.remove(name)
        } else {
            setter(name, value)
        }

    }

    @Synchronized
    private fun init(bundle: Bundle) {
        if (initialized) return
        initialized = true

        val getterProvider = getterProvider ?: error("Provider is null.")
        val setterProvider = setterProvider ?: error("Provider is null.")

        getter = getterProvider(bundle)
        setter = setterProvider(bundle)

        this.getterProvider = null
        this.setterProvider = null

    }

}

inline fun <reified T> bundleCommonDefaultValue(): T {
    return if (isNullable<T>())  null as T
    else error("Doesn't have default value.")
}