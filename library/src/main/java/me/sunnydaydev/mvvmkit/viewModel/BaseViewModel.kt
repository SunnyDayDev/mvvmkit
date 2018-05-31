package me.sunnydaydev.mvvmkit.viewModel

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.ViewModel
import android.databinding.Bindable
import android.databinding.Observable
import android.databinding.PropertyChangeRegistry
import android.support.annotation.CallSuper
import me.sunnydaydev.mvvmkit.observable.NotifiableObservable
import me.sunnydaydev.modernrx.*

/**
 * Created by sunny on 28.04.2018.
 * mail: mail@sunnydaydev.me
 */

abstract class BaseViewModel: ViewModel(), LifecycleObserver, NotifiableObservable, ModernRx {

    private val disposerBag = DisposableBag()
    final override val modernRxDisposer: ModernRx.Disposer = ModernRx.Disposer(disposerBag)

    // region Observable

    @Transient
    private var callbacks = PropertyChangeRegistry()

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    override fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with [Bindable] to generate a field in
     * `BR` to be used as `fieldId`.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    override fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    // endregion Observable

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        disposerBag.enabled = false
    }

}