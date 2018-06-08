package me.sunnydaydev.mvvmkit.sample.vm

import androidx.databinding.Bindable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.sunnydaydev.mvvmkit.observable.bindable
import me.sunnydaydev.mvvmkit.viewModel.MVVMViewModel
import java.util.concurrent.TimeUnit

/**
 * Created by sunny on 08.06.2018.
 * mail: mail@sunnydaydev.me
 */

internal class BlackViewModel: MVVMViewModel() {

    @get:Bindable var id by bindable("1")

    private val timerDisposable: Disposable = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                id = "$it"
            }

    internal fun clear() = onCleared()

    override fun onCleared() {
        super.onCleared()
        timerDisposable.dispose()
    }

    class Factory: ColorsFactory {

        override fun create(): MVVMViewModel = BlackViewModel()

    }

}