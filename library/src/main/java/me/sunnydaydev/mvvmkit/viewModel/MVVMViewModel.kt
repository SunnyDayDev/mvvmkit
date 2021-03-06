package me.sunnydaydev.mvvmkit.viewModel

import androidx.lifecycle.ViewModel
import androidx.databinding.Observable
import me.sunnydaydev.mvvmkit.observable.NotifiableObservable

/**
 * Created by sunny on 28.04.2018.
 * mail: mail@sunnydaydev.me
 */

abstract class MVVMViewModel: ViewModel(), NotifiableObservable {

    private val registry by lazy { NotifiableObservable.Registry(this) }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) =
            registry.addOnPropertyChangedCallback(callback)

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) =
            registry.removeOnPropertyChangedCallback(callback)

    override fun notifyChange() = registry.notifyChange()

    override fun notifyPropertyChanged(fieldId: Int) = registry.notifyPropertyChanged(fieldId)

    fun <T: MVVMViewModel> T.clearViewModel() = this.onCleared()

}