package me.sunnydaydev.mvvmkit.property

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import me.sunnydaydev.mvvmkit.util.isNullable
import org.parceler.Parcels
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 30.07.2018.
 * mail: mail@sunnydaydev.me
 */

inline fun <reified T: Boolean?> bundleBoolean(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = bundleProperty<T, Boolean>(
        name = name,
        defaultValue = defaultValue,
        set = { bundle -> { key, value -> bundle.putBoolean(key, value) } },
        get = { bundle -> { key -> bundle.getBoolean(key) } }
)

inline fun <reified T: Int?> bundleInt(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = bundleProperty<T, Int>(
        name = name,
        defaultValue = defaultValue,
        set = { bundle -> { key, value -> bundle.putInt(key, value) } },
        get = { bundle -> { key -> bundle.getInt(key) } }
)

inline fun <reified T: Long?> bundleLong(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = bundleProperty<T, Long>(
        name = name,
        defaultValue = defaultValue,
        set = { bundle -> { key, value -> bundle.putLong(key, value) } },
        get = { bundle -> { key -> bundle.getLong(key) } }
)

inline fun <reified T: Float?> bundleFloat(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = bundleProperty<T, Float>(
        name = name,
        defaultValue = defaultValue,
        set = { bundle -> { key, value -> bundle.putFloat(key, value) } },
        get = { bundle -> { key -> bundle.getFloat(key) } }
)

inline fun <reified T: Double?> bundleDouble(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = bundleProperty<T, Double>(
        name = name,
        defaultValue = defaultValue,
        set = { bundle -> { key, value -> bundle.putDouble(key, value) } },
        get = { bundle -> { key -> bundle.getDouble(key) } }
)

inline fun <reified T: String?> bundleString(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = bundleProperty<T, String>(
        name = name,
        defaultValue = defaultValue,
        set = { bundle -> { key, value -> bundle.putString(key, value) } },
        get = { bundle -> { key -> bundle.getString(key)!! } }
)

inline fun <reified T: Parcelable?> bundleParcellable(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = bundleProperty<T, Parcelable>(
        name = name,
        defaultValue = defaultValue,
        set = { bundle -> { key, value -> bundle.putParcelable(key, value) } },
        get = { bundle -> { key -> bundle.getParcelable(key)!! } }
)


inline fun <reified T: Any?> bundleParcels(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = bundleProperty<T, Any>(
        name = name,
        defaultValue = defaultValue,
        set = { bundle ->
            { key, value ->
                val parceled = Parcels.wrap(value)
                bundle.putParcelable(key, parceled)
            }
        },
        get = { bundle ->
            { key ->
                val parceled: Parcelable = bundle.getParcelable(key)!!
                Parcels.unwrap(parceled)
            }
        }
)

inline fun <reified T: TNN?, TNN: Any> bundleProperty(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue,
        noinline set: (bundle: Bundle) -> (key: String, value: TNN) -> Unit,
        noinline get: (bundle: Bundle) -> (key: String) -> TNN
):ReadWriteProperty<Bundle, T> = BundleProperty<T, TNN>(name, defaultValue, set, get)

class BundleProperty<T: TNN?, TNN: Any>(
        private val name: String,
        private val defaultValue: () -> T,
        private val setterProvider: (bundle: Bundle) -> (key: String, value: TNN) -> Unit,
        private val getterProvider: (bundle: Bundle) -> (key: String) -> TNN
): ReadWriteProperty<Bundle, T> {

    override fun getValue(thisRef: Bundle, property: KProperty<*>): T {

        return if (thisRef.containsKey(name)) getterProvider(thisRef)(name) as T
               else defaultValue()

    }

    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: T) {

        if (value == null) {
            thisRef.remove(name)
        } else {
            setterProvider(thisRef)(name, value)
        }

    }

}

inline fun <reified T> commonPropertyDefaultValue(): T {
    return if (isNullable<T>())  null as T
    else error("Doesn't have default value.")
}

fun <T: Fragment> T.withArguments(build: Bundle.() -> Unit): T {
    val bundle = arguments ?: Bundle()
    arguments = bundle.apply(build)
    return this
}

fun Intent.withExtras(build: Bundle.() -> Unit): Intent {
    val bundle = extras ?: Bundle()
    putExtras(bundle.apply(build))
    return this
}