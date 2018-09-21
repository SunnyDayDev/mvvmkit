package me.sunnydaydev.mvvmkit.property

import android.content.Intent
import android.os.Parcelable
import org.parceler.Parcels
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 30.07.2018.
 * mail: mail@sunnydaydev.me
 */

inline fun <reified T: Boolean?> intentBool(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = intentProperty<T, Boolean>(
        name = name,
        defaultValue = defaultValue,
        set = { intent -> { key, value -> intent.putExtra(key, value) } },
        get = { intent -> { key -> intent.getBooleanExtra(key, false) } }
)

inline fun <reified T: Int?> intentInt(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = intentProperty<T, Int>(
        name = name,
        defaultValue = defaultValue,
        set = { intent -> { key, value -> intent.putExtra(key, value) } },
        get = { intent -> { key -> intent.getIntExtra(key, 0) } }
)

inline fun <reified T: Long?> intentLong(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = intentProperty<T, Long>(
        name = name,
        defaultValue = defaultValue,
        set = { intent -> { key, value -> intent.putExtra(key, value) } },
        get = { intent -> { key -> intent.getLongExtra(key, 0) } }
)

inline fun <reified T: Float?> intentFloat(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = intentProperty<T, Float>(
        name = name,
        defaultValue = defaultValue,
        set = { intent -> { key, value -> intent.putExtra(key, value) } },
        get = { intent -> { key -> intent.getFloatExtra(key, 0.0f) } }
)

inline fun <reified T: Double?> intentDouble(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = intentProperty<T, Double>(
        name = name,
        defaultValue = defaultValue,
        set = { intent -> { key, value -> intent.putExtra(key, value) } },
        get = { intent -> { key -> intent.getDoubleExtra(key, 0.0) } }
)

inline fun <reified T: String?> intentString(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = intentProperty<T, String>(
        name = name,
        defaultValue = defaultValue,
        set = { intent -> { key, value -> intent.putExtra(key, value) } },
        get = { intent -> { key -> intent.getStringExtra(key)!! } }
)

inline fun <reified T: Parcelable?> intentParcellable(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = intentProperty<T, Parcelable>(
        name = name,
        defaultValue = defaultValue,
        set = { intent -> { key, value -> intent.putExtra(key, value) } },
        get = { intent -> { key -> intent.getParcelableExtra(key)!! } }
)


inline fun <reified T: Any?> intentParcels(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue
) = intentProperty<T, Any>(
        name = name,
        defaultValue = defaultValue,
        set = { intent ->
            { key, value ->
                val parceled = Parcels.wrap(value)
                intent.putExtra(key, parceled)
            }
        },
        get = { intent ->
            { key ->
                val parceled: Parcelable = intent.getParcelableExtra(key)!!
                Parcels.unwrap(parceled)
            }
        }
)

inline fun <reified T: TNN?, TNN: Any> intentProperty(
        name: String,
        noinline defaultValue: () -> T = ::commonPropertyDefaultValue,
        noinline set: (intent: Intent) -> (key: String, value: TNN) -> Unit,
        noinline get: (intent: Intent) -> (key: String) -> TNN
):ReadWriteProperty<Intent, T> = IntentProperty<T, TNN>(name, defaultValue, set, get)

class IntentProperty<T: TNN?, TNN: Any>(
        private val name: String,
        private val defaultValue: () -> T,
        private val setterProvider: (intent: Intent) -> (key: String, value: TNN) -> Unit,
        private val getterProvider: (intent: Intent) -> (key: String) -> TNN
): ReadWriteProperty<Intent, T> {

    override fun getValue(thisRef: Intent, property: KProperty<*>): T {

        return if (thisRef.hasExtra(name)) getterProvider(thisRef)(name) as T
               else defaultValue()

    }

    override fun setValue(thisRef: Intent, property: KProperty<*>, value: T) {

        if (value == null) {
            thisRef.removeExtra(name)
        } else {
            setterProvider(thisRef)(name, value)
        }

    }

}