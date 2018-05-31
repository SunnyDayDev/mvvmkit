package me.sunnydaydev.mvvmkit.viewModel

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.ViewModel
import android.databinding.Observable
import me.sunnydaydev.mvvmkit.observable.NotifiableObservable

/**
 * Created by sunny on 28.04.2018.
 * mail: mail@sunnydaydev.me
 */

abstract class MVVMViewModel: ViewModel(), LifecycleObserver, NotifiableObservable {

    private val registry by lazy { NotifiableObservable.Registry(this) }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) =
            registry.addOnPropertyChangedCallback(callback)

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) =
            registry.removeOnPropertyChangedCallback(callback)

    override fun notifyChange() = registry.notifyChange()

    override fun notifyPropertyChanged(fieldId: Int) = registry.notifyPropertyChanged(fieldId)

    // endregion Observable

    open fun onBackPressed(): Boolean = false

}