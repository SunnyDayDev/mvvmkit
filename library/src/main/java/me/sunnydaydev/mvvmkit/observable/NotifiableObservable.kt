package me.sunnydaydev.mvvmkit.observable

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry

/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnydaydev.me
 */

interface NotifiableObservable: Observable {

    fun notifyChange()

    fun notifyPropertyChanged(fieldId: Int)

    class Registry(private var notifiable: NotifiableObservable): NotifiableObservable {

        @Transient
        private lateinit var callbacks: PropertyChangeRegistry

        private val initialized get() = ::callbacks.isInitialized

        override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
            synchronized(this) {
                if (!initialized) {
                    callbacks = PropertyChangeRegistry()
                }
            }
            callbacks.add(callback)
        }

        override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
            synchronized(this) {
                if (!initialized) {
                    return
                }
            }
            callbacks.remove(callback)
        }

        /**
         * Notifies listeners that all properties of this instance have changed.
         */
        override fun notifyChange() {
            synchronized(this) {
                if (!initialized) {
                    return
                }
            }
            callbacks.notifyCallbacks(notifiable, 0, null)
        }

        /**
         * Notifies listeners that a specific property has changed. The getter for the property
         * that changes should be marked with [Bindable] to generate a field in
         * `BR` to be used as `fieldId`.
         *
         * @param fieldId The generated BR id for the Bindable field.
         */
        override fun notifyPropertyChanged(fieldId: Int) {
            synchronized(this) {
                if (!initialized) {
                    return
                }
            }
            callbacks.notifyCallbacks(notifiable, fieldId, null)
        }

    }

}

open class NotifiableBaseObservable: BaseObservable(), NotifiableObservable