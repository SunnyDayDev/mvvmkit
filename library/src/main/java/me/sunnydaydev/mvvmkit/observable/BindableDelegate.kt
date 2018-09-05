package me.sunnydaydev.mvvmkit.observable

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by sunny on 28.04.2018.
 * mail: mail@sunnydaydev.me
 */

internal class BindableDelegate<in R: NotifiableObservable, T: Any?> (
        private var value: T,
        private var id: Int?,
        private val onChange: ((T) -> Unit)? = null
): ReadWriteProperty<R, T> {

    companion object {

        private val dataBindingFields by lazy<Map<String, Int>> {

            val clazz = try {
                Class.forName("androidx.databinding.library.baseAdapters.BR")
            } catch (e: Throwable) {
                null
            } ?: return@lazy emptyMap()

            clazz.fields.associate { it.name to it.getInt(null) }

        }

    }

    private var checkedId: Int? = null

    override operator fun getValue(thisRef: R, property: KProperty<*>): T = this.value

    override operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {

        this.value = value

        id = id ?: checkAndGetId(property)

        thisRef.notifyPropertyChanged(id!!)

        onChange?.invoke(value)

    }

    private fun checkAndGetId(property: KProperty<*>): Int {

        return checkedId ?: id
                ?: dataBindingFields[property.name]?.also { checkedId = it }
                ?: dataBindingFields[property.fallbackName]?.also { checkedId = it }
                ?: throw IllegalStateException("Unknown bindable property.")

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