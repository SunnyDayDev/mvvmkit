package me.sunnydaydev.mvvmkit.observable

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import timber.log.Timber

/**
 * Created by sunny on 28.04.2018.
 * mail: mail@sunnydaydev.me
 */

internal class BindableDelegate<in R: NotifiableObservable, T: Any?> (
        private var value: T,
        private val id: Int?,
        private val onChange: ((T) -> Unit)? = null
): ReadWriteProperty<R, T> {

    companion object {

        private val dataBindingFields by lazy<Map<String, Int>> {

            val clazz = try {
                Class.forName("androidx.databinding.library.baseAdapters.BR")
            } catch (e: Throwable) {
                Timber.e(e)
                null
            } ?: return@lazy emptyMap()

            clazz.fields.associate { it.name to it.getInt(null) }

        }

    }

    private var cachedCheckedId: Int? = null

    override operator fun getValue(thisRef: R, property: KProperty<*>): T = this.value

    override operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {

        this.value = value

        val checkedId = getBindablePropertyId(property)

        thisRef.notifyPropertyChanged(checkedId)

        onChange?.invoke(value)

    }

    private fun getBindablePropertyId(property: KProperty<*>): Int {

        val cached = cachedCheckedId

        return if (cached != null) cached
        else {

            val checkedId = id
                    ?: dataBindingFields[property.name]
                    ?: dataBindingFields[property.fallbackName]
                    ?: throw IllegalStateException("Unknown bindable property: $property")

            checkedId.also { cachedCheckedId = it }

        }

    }

    private val KProperty<*>.fallbackName: String get() {
        if (!name.startsWith("is")) return name
        return name[2].toLowerCase() + name.substring(3)
    }


}

fun <R: NotifiableObservable, T: Any?> bindable(
        initialValue: T,
        onChange: ((T) -> Unit)? = null
): ReadWriteProperty<R, T> = bindable(initialValue, null, onChange)

fun <R: NotifiableObservable, T: Any?> bindable(
        initialValue: T,
        id: Int?,
        onChange: ((T) -> Unit)? = null
): ReadWriteProperty<R, T> = BindableDelegate(initialValue, id, onChange)